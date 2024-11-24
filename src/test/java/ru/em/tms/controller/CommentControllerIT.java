package ru.em.tms.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import ru.em.tms.TMSApp;
import ru.em.tms.TestTMSApp;
import ru.em.tms.lib.mapper.CommentMapper;
import ru.em.tms.lib.mapper.TaskMapper;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.comment.CommentEditDTO;
import ru.em.tms.model.dto.comment.CommentGetDTO;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;
import ru.em.tms.service.CommentService;
import ru.em.tms.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TestTMSApp.class, TMSApp.class})
@AutoConfigureMockMvc
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@WithMockUser(username = "admin@test.ru", password = "admin", authorities = "ADMIN")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommentControllerIT {
    private final MockMvc mockMvc;
    private final CommentService service;
    private final ApplicationContext context;
    private final UserService userService;
    private final ObjectMapper mapper;

    private final List<UserGetDTO> initialUsers = new ArrayList<>(3);
    private final List<TaskGetDTO> initialTasks = new ArrayList<>(3);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @BeforeAll
    void setUp() {
        initialUsers.add(userService.create(UserEditDTO.builder()
                .email("admin@test.ru").password("admin").role(Role.ADMIN).build()));

        for (int i = 0; i < 2; i++) {
            var password = context.getBean(PasswordEncoder.class).encode("user" + i);

            initialUsers.add(userService.create(UserEditDTO.builder()
                    .email("user" + i + "@test.ru").password(password).role(Role.USER).build()));
        }

        var taskMapper = context.getBean(TaskMapper.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var admin = userRepo.findById(initialUsers.getFirst().getId()).get();
        for (int i = 0; i < initialUsers.size(); i++) {
            var user = i > 0 ? userRepo.findById(initialUsers.get(i).getId()).get() : admin;

            initialTasks.add(taskMapper.sourceToDestination(taskRepo.save(Task.builder()
                    .title("task")
                    .description("task")
                    .status(Status.NEW)
                    .priority(Priority.LOW)
                    .author(admin)
                    .executor(user)
                    .build())));
        }
    }

    @Test
    void getAll_whenAdmin_returnsAll() throws Exception {
        var task = initialTasks.getFirst();
        var commentsExcepted = new ArrayList<CommentGetDTO>(){{
            for (int i = 1; i <= 10; i++) {
                add(service.create(task.getId(), CommentEditDTO.builder()
                        .content("comment" + i)
                        .build()));
            }
        }};
        var excepted = new PageableResponse<>(commentsExcepted, 1, 0, 10);

        var content = mockMvc.perform(get("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(new LinkedMultiValueMap<>(){{
                            put("page", Collections.singletonList("0"));
                            put("size", Collections.singletonList("10"));
                        }}))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, new TypeReference<PageableResponse<CommentGetDTO>>() {});

        Assertions.assertEquals(excepted, actual);
    }

    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void getAll_whenUserHasAccess_returnsAll() throws Exception {
        var task = initialTasks.get(2);
        var commentMapper = context.getBean(CommentMapper.class);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var commentsExcepted = new ArrayList<CommentGetDTO>(){{
            for (int i = 1; i <= 2; i++) {
                add(commentMapper.sourceToDestination(commentRepo.save(Comment.builder()
                        .content("comment" + i)
                        .task(taskRepo.findById(task.getId()).get())
                        .author(i % 2 == 0 ? userRepo.findByEmail("user0@test.ru").get() :
                                userRepo.findByEmail("user1@test.ru").get())
                        .build())));
            }
        }};
        var excepted = new PageableResponse<>(commentsExcepted, 1, 0, 10);

        var content = mockMvc.perform(get("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(new LinkedMultiValueMap<>(){{
                            put("page", Collections.singletonList("0"));
                            put("size", Collections.singletonList("10"));
                        }}))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, new TypeReference<PageableResponse<CommentGetDTO>>() {});

        Assertions.assertEquals(excepted, actual);
    }

    @Test
    @WithMockUser(username = "user0@test.ru", password = "user2", authorities = "USER")
    void getAll_whenUserHasNoAccess_returnsError() throws Exception {
        var task = initialTasks.get(2);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        for (int i = 1; i <= 2; i++) {
            commentRepo.save(Comment.builder()
                    .content("comment" + i)
                    .task(taskRepo.findById(task.getId()).get())
                    .author(i % 2 == 0 ? userRepo.findByEmail("user0@test.ru").get() :
                            userRepo.findByEmail("user1@test.ru").get())
                    .build());
        }

        mockMvc.perform(get("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParams(new LinkedMultiValueMap<>(){{
                            put("page", Collections.singletonList("0"));
                            put("size", Collections.singletonList("10"));
                        }}))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void create_whenAdminAndCorrect_returnsDTO() throws Exception {
        var task = initialTasks.getFirst();
        var commentEditDTO = CommentEditDTO.builder()
                .content("comment")
                .build();

        var content = mockMvc.perform(post("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, CommentGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actual.getId()),
                () -> Assertions.assertEquals(task.getId(), actual.getTaskId()),
                () -> Assertions.assertEquals(commentEditDTO.getContent(), actual.getContent()),
                () -> Assertions.assertEquals(initialUsers.getFirst().getId(), actual.getAuthorId())
        );
    }

    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void create_whenUserAndHasAccessAndCorrect_returnsDTO() throws Exception {
        var task = initialTasks.get(2);
        var commentEditDTO = CommentEditDTO.builder()
                .content("comment")
                .build();

        var content = mockMvc.perform(post("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, CommentGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actual.getId()),
                () -> Assertions.assertEquals(task.getId(), actual.getTaskId()),
                () -> Assertions.assertEquals(commentEditDTO.getContent(), actual.getContent()),
                () -> Assertions.assertEquals(initialUsers.get(2).getId(), actual.getAuthorId())
        );
    }

    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void create_whenUserAndHasNoAccessAndCorrect_returnsError() throws Exception {
        var task = initialTasks.get(1);
        var commentEditDTO = CommentEditDTO.builder()
                .content("comment")
                .build();

        mockMvc.perform(post("/tasks/" + task.getId() + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void update_whenAdminAndCorrect_returnsUpdated() throws Exception {
        var task = initialTasks.getFirst();
        var created = service.create(task.getId(), CommentEditDTO.builder()
                .content("comment1")
                .build());
        var commentEditDTO = CommentEditDTO.builder()
                .content("comment2")
                .build();

        var content = mockMvc.perform(put("/tasks/" + task.getId() + "/comments/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, CommentGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(created.getId(), actual.getId()),
                () -> Assertions.assertEquals(task.getId(), actual.getTaskId()),
                () -> Assertions.assertEquals(created.getAuthorId(), actual.getAuthorId()),
                () -> Assertions.assertEquals(commentEditDTO.getContent(), actual.getContent())
        );
    }

    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void update_whenUserAndHasAccessAndCorrect_returnsUpdated() throws Exception {
        var commentMapper = context.getBean(CommentMapper.class);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var task = initialTasks.get(2);
        var created = commentMapper.sourceToDestination(commentRepo.save(Comment.builder()
                .content("comment1")
                .task(taskRepo.findById(task.getId()).get())
                .author(userRepo.findById(initialUsers.get(2).getId()).get())
                .build()));

        var commentEditDTO = CommentEditDTO.builder()
                .content("comment2")
                .build();

        var content = mockMvc.perform(put("/tasks/" + task.getId() + "/comments/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, CommentGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(created.getId(), actual.getId()),
                () -> Assertions.assertEquals(task.getId(), actual.getTaskId()),
                () -> Assertions.assertEquals(created.getAuthorId(), actual.getAuthorId()),
                () -> Assertions.assertEquals(commentEditDTO.getContent(), actual.getContent())
        );
    }

    @Test
    @WithMockUser(username = "user0@test.ru", password = "user2", authorities = "USER")
    void update_whenUserAndHasNoAccessAndCorrect_returnsUpdated() throws Exception {
        var commentMapper = context.getBean(CommentMapper.class);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var task = initialTasks.get(1);
        var created = commentMapper.sourceToDestination(commentRepo.save(Comment.builder()
                .content("comment1")
                .task(taskRepo.findById(task.getId()).get())
                .author(userRepo.findById(initialUsers.get(2).getId()).get())
                .build()));

        var commentEditDTO = CommentEditDTO.builder()
                .content("comment2")
                .build();

        mockMvc.perform(put("/tasks/" + task.getId() + "/comments/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentEditDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void delete_whenAdminAndCorrect_returnsOk() throws Exception {
        var task = initialTasks.getFirst();
        var created = service.create(task.getId(), CommentEditDTO.builder()
                .content("comment")
                .build());

        mockMvc.perform(delete("/tasks/" + task.getId() + "/comments/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(service.getById(task.getId(), created.getId()), Optional.empty());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void delete_whenUserAuthorAndCorrect_returnsOk() throws Exception {
        var commentMapper = context.getBean(CommentMapper.class);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var task = initialTasks.getFirst();
        var created = commentMapper.sourceToDestination(commentRepo.save(Comment.builder()
                .content("comment")
                .task(taskRepo.findById(task.getId()).get())
                .author(userRepo.findById(initialUsers.get(2).getId()).get())
                .build()));

        mockMvc.perform(delete("/tasks/" + task.getId() + "/comments/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(service.getById(task.getId(), created.getId()), Optional.empty());
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @WithMockUser(username = "user1@test.ru", password = "user2", authorities = "USER")
    void delete_whenUserNotAuthorAndCorrect_returnsError() throws Exception {
        var commentMapper = context.getBean(CommentMapper.class);
        var commentRepo = context.getBean(CommentRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var task = initialTasks.getFirst();
        var created = commentMapper.sourceToDestination(commentRepo.save(Comment.builder()
                .content("comment")
                .task(taskRepo.findById(task.getId()).get())
                .author(userRepo.findById(initialUsers.get(1).getId()).get())
                .build()));

        mockMvc.perform(delete("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        Assertions.assertNotNull(service.getById(task.getId(), created.getId()).orElse(null));
    }

    @AfterAll
    void tearDown() {
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);

        taskRepo.deleteAll();
        userRepo.deleteAll();
    }
}
