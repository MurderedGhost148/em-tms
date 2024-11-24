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
import ru.em.tms.lib.mapper.TaskMapper;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.dto.PageableResponse;
import ru.em.tms.model.dto.task.TaskCreateDTO;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.dto.task.TaskUpdateDTO;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;
import ru.em.tms.service.TaskService;
import ru.em.tms.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {TestTMSApp.class, TMSApp.class})
@AutoConfigureMockMvc
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@WithMockUser(username = "admin@test.ru", password = "admin", authorities = "ADMIN")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskControllerIT {
    private final MockMvc mockMvc;
    private final TaskService service;
    private final ApplicationContext context;
    private final UserService userService;
    private final ObjectMapper mapper;

    private final List<UserGetDTO> initialUsers = new ArrayList<>(5);

    @BeforeAll
    void setUp() {
        initialUsers.add(userService.create(UserEditDTO.builder()
                .email("admin@test.ru").password("admin").role(Role.ADMIN).build()));

        for (int i = 0; i < 4; i++) {
            var password = context.getBean(PasswordEncoder.class).encode("user" + i);

            initialUsers.add(userService.create(UserEditDTO.builder()
                    .email("user" + i + "@test.ru").password(password).role(Role.USER).build()));
        }
    }

    @Test
    void getAll_whenAdmin_returnsAll() throws Exception {
        var tasksExcepted = new ArrayList<TaskGetDTO>(){{
            for (int i = 1; i <= 10; i++) {
                add(service.create(TaskCreateDTO.builder()
                        .title("task №" + i)
                        .executorId(initialUsers.get(i % 4).getId())
                        .build()));
            }
        }};
        var excepted = new PageableResponse<>(tasksExcepted, 1, 0, 10);

        var content = mockMvc.perform(get("/tasks")
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

        var actual = mapper.readValue(content, new TypeReference<PageableResponse<TaskGetDTO>>() {});

        Assertions.assertEquals(excepted, actual);
    }

    @Test
    void getAll_whenAdminAndExecutorFilter_returnsAllFiltered() throws Exception {
        var executorId = initialUsers.getFirst().getId();
        var tasksExcepted = new ArrayList<TaskGetDTO>(){{
            for (int i = 1; i <= 10; i++) {
                add(service.create(TaskCreateDTO.builder()
                        .title("task №" + i)
                        .executorId(initialUsers.get(i % 4).getId())
                        .build()));
            }
        }}.stream().filter(t -> Objects.equals(t.getExecutorId(), executorId)).toList();
        var excepted = new PageableResponse<>(tasksExcepted, 1, 0, 10);

        var content = mockMvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParams(new LinkedMultiValueMap<>(){{
                            put("page", Collections.singletonList("0"));
                            put("size", Collections.singletonList("10"));
                            put("executorId", Collections.singletonList(executorId.toString()));
                        }}))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, new TypeReference<PageableResponse<TaskGetDTO>>() {});

        Assertions.assertEquals(excepted, actual);
    }

    @Test
    void create_whenAdminAndCorrect_returnsCreated() throws Exception {
        var taskCreateDTO = TaskCreateDTO.builder()
                .title("task")
                .description("task")
                .priority(Priority.LOW)
                .executorId(initialUsers.get(1).getId())
                .build();

        var content = mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, TaskGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertNotNull(actual.getId()),
                () -> Assertions.assertEquals(taskCreateDTO.getTitle(), actual.getTitle()),
                () -> Assertions.assertEquals(taskCreateDTO.getDescription(), actual.getDescription()),
                () -> Assertions.assertEquals(taskCreateDTO.getPriority(), actual.getPriority()),
                () -> Assertions.assertEquals(taskCreateDTO.getExecutorId(), actual.getExecutorId())
        );
    }

    @Test
    @WithMockUser(username = "user2@test.ru", password = "user2", authorities = "USER")
    void create_whenUserAndCorrect_returnsError() throws Exception {
        var taskCreateDTO = TaskCreateDTO.builder()
                .title("task")
                .description("task")
                .priority(Priority.LOW)
                .executorId(initialUsers.get(1).getId())
                .build();

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskCreateDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void update_whenAdminAndCorrect_returnsUpdated() throws Exception {
        var created = service.create(TaskCreateDTO.builder()
                .title("task")
                .description("task")
                .priority(Priority.LOW)
                .executorId(initialUsers.get(1).getId())
                .build());
        var taskUpdateDTO = TaskUpdateDTO.builder()
                .title(created.getTitle())
                .description(created.getDescription())
                .status(created.getStatus())
                .priority(Priority.MEDIUM)
                .executorId(initialUsers.get(2).getId())
                .build();

        var content = mockMvc.perform(put("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, TaskGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(created.getId(), actual.getId()),
                () -> Assertions.assertEquals(taskUpdateDTO.getTitle(), actual.getTitle()),
                () -> Assertions.assertEquals(taskUpdateDTO.getStatus(), actual.getStatus()),
                () -> Assertions.assertEquals(taskUpdateDTO.getDescription(), actual.getDescription()),
                () -> Assertions.assertEquals(taskUpdateDTO.getPriority(), actual.getPriority()),
                () -> Assertions.assertEquals(taskUpdateDTO.getExecutorId(), actual.getExecutorId())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @WithMockUser(username = "user1@test.ru", password = "user1", roles = "USER")
    void update_whenUserAndHasAccessToTask_returnsUpdated() throws Exception {
        var taskMapper = context.getBean(TaskMapper.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var created = taskMapper.sourceToDestination(taskRepo.save(Task.builder()
                .title("task")
                .description("task")
                .status(Status.NEW)
                .priority(Priority.LOW)
                .author(userRepo.findByEmail("admin@test.ru").get())
                .executor(userRepo.findByEmail("user1@test.ru").get())
                .build()));
        var taskUpdateDTO = TaskUpdateDTO.builder()
                .title(created.getTitle())
                .description(created.getDescription())
                .status(Status.IN_PROGRESS)
                .priority(created.getPriority())
                .executorId(initialUsers.get(3).getId())
                .build();

        var content = mockMvc.perform(put("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        var actual = mapper.readValue(content, TaskGetDTO.class);

        Assertions.assertAll(
                () -> Assertions.assertEquals(created.getId(), actual.getId()),
                () -> Assertions.assertEquals(created.getTitle(), actual.getTitle()),
                () -> Assertions.assertEquals(taskUpdateDTO.getStatus(), actual.getStatus()),
                () -> Assertions.assertEquals(created.getDescription(), actual.getDescription()),
                () -> Assertions.assertEquals(created.getPriority(), actual.getPriority()),
                () -> Assertions.assertEquals(created.getExecutorId(), actual.getExecutorId())
        );
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    @WithMockUser(username = "user2@test.ru", password = "user2", authorities = "USER")
    void update_whenUserAndHasNotAccessToTask_returnsError() throws Exception {
        var taskMapper = context.getBean(TaskMapper.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var created = taskMapper.sourceToDestination(taskRepo.save(Task.builder()
                .title("task")
                .description("task")
                .status(Status.NEW)
                .priority(Priority.LOW)
                .author(userRepo.findByEmail("admin@test.ru").get())
                .executor(userRepo.findByEmail("user1@test.ru").get())
                .build()));
        var taskUpdateDTO = TaskUpdateDTO.builder()
                .title(created.getTitle())
                .description(created.getDescription())
                .status(created.getStatus())
                .priority(Priority.MEDIUM)
                .executorId(initialUsers.get(3).getId())
                .build();

        mockMvc.perform(put("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(taskUpdateDTO)))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());

        Assertions.assertEquals(service.getById(created.getId()), Optional.of(created));
    }

    @Test
    void delete_whenAdminAndCorrect_returnsOk() throws Exception {
        var created = service.create(TaskCreateDTO.builder()
                .title("task")
                .description("task")
                .priority(Priority.LOW)
                .executorId(initialUsers.get(1).getId())
                .build());

        mockMvc.perform(delete("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Assertions.assertEquals(service.getById(created.getId()), Optional.empty());
    }

    @Test
    @WithMockUser(username = "user2@test.ru", password = "user2", authorities = "USER")
    void delete_whenUserAndCorrect_returnsError() throws Exception {
        var taskMapper = context.getBean(TaskMapper.class);
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);
        var created = taskMapper.sourceToDestination(taskRepo.save(Task.builder()
                .title("task")
                .description("task")
                .status(Status.NEW)
                .priority(Priority.LOW)
                .author(userRepo.findByEmail("admin@test.ru").get())
                .executor(userRepo.findByEmail("user1@test.ru").get())
                .build()));

        mockMvc.perform(delete("/tasks/" + created.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.FORBIDDEN.value()));

        Assertions.assertNotNull(service.getById(created.getId()).orElse(null));
    }

    @AfterAll
    void tearDown() {
        var taskRepo = context.getBean(TaskRepo.class);
        var userRepo = context.getBean(UserRepo.class);

        taskRepo.deleteAll();
        userRepo.deleteAll();
    }
}