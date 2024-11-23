package ru.em.tms.service;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.em.tms.lib.filter.db.TaskFilter;
import ru.em.tms.lib.mapper.TaskMapper;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.task.TaskCreateDTO;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.dto.task.TaskUpdateDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepo repo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private TaskMapper mapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private TaskService service;

    @BeforeEach
    void setUp() {
        var email = "test@test.ru";
        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        var user = User.builder().email(email).role(Role.ADMIN).build();
        lenient().when(userService.getCurrentUser()).thenReturn(user);
    }

    @Test
    void getAll_whenAccessiblePageableParams_returnsAll() {
        var pageable = PageRequest.of(0, 10);
        var tasks = new LinkedList<Task>(){{
            for(int i = 0; i < 10; i++) add(new Task());
        }};
        var pageExcepted = new PageImpl<>(tasks, pageable, tasks.size() + 1);

        when(repo.findAll(any(Specification.class),eq(pageable))).thenReturn(pageExcepted);

        var pageActual = service.getAll(pageable, new TaskFilter(null, null));

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(pageExcepted.getContent().size())
        );
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAll_whenNotAccessiblePageableParams_returnsNone() {
        var pageable = PageRequest.of(1, 10);
        var tasks = new LinkedList<Task>();
        var pageExcepted = new PageImpl<>(tasks, pageable, 0);

        when(repo.findAll(any(Specification.class),eq(pageable))).thenReturn(pageExcepted);

        var pageActual = service.getAll(pageable, new TaskFilter(null, null));

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(pageExcepted.getContent().size())
        );
        verify(repo).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void getAll_whenFoundByFilterParams_returnsAll() {
        var authorId = 1;
        var pageable = PageRequest.of(0, 10);
        var tasks = new LinkedList<Task>(){{
            var author = User.builder().id(authorId).build();

            for(int i = 0; i < 9; i++) add(Task.builder().author(author).build());

            author = User.builder().id(2).build();
            add(Task.builder().author(author).build());
        }};
        var tasksExcepted = tasks.stream().filter(t -> t.getAuthor().getId() == authorId).toList();
        var pageExcepted = new PageImpl<>(tasksExcepted, pageable, tasksExcepted.size());

        when(repo.findAll(any(Specification.class), eq(pageable))).thenReturn(pageExcepted);
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);
            return TaskGetDTO.builder().authorId(task.getAuthor().getId()).build();
        });

        var pageActual = service.getAll(pageable, new TaskFilter(authorId, null));

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(tasksExcepted.size()),
                () -> org.junit.jupiter.api.Assertions.assertTrue(pageActual.getResult().stream()
                        .anyMatch(t -> Objects.equals(authorId, t.getAuthorId())))
        );
    }

    @Test
    void getAll_whenNotFoundByFilterParams_returnsEmpty() {
        var authorId = 3;
        var pageable = PageRequest.of(0, 10);
        var tasks = new LinkedList<Task>(){{
            var author = User.builder().id(1).build();
            for(int i = 0; i < 9; i++) add(Task.builder().author(author).build());

            author = User.builder().id(2).build();
            add(Task.builder().author(author).build());
        }};
        var tasksExcepted = tasks.stream().filter(t -> t.getAuthor().getId() == authorId).toList();
        var pageExcepted = new PageImpl<>(tasksExcepted, pageable, tasksExcepted.size());

        when(repo.findAll(any(Specification.class), eq(pageable))).thenReturn(pageExcepted);

        var pageActual = service.getAll(pageable, new TaskFilter(authorId, null));

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> org.junit.jupiter.api.Assertions.assertTrue(pageActual.getResult().isEmpty())
        );
    }

    @Test
    void getById_whenTaskExists_returnsTask() {
        var taskId = 1L;
        var task = Task.builder().id(taskId).build();

        when(repo.findById(taskId)).thenReturn(Optional.of(task));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation ->
                TaskGetDTO.builder().id(((Task) invocation.getArgument(0)).getId()).build());

        var actual = service.getById(taskId);

        Assertions.assertThat(actual).isPresent();
        Assertions.assertThat(actual.get().getId()).isEqualTo(taskId);
        verify(repo).findById(taskId);
    }

    @Test
    void getById_whenTaskNotExists_returnsNone() {
        var taskId = 1L;
        when(repo.findById(taskId)).thenReturn(Optional.empty());

        var actual = service.getById(taskId);

        Assertions.assertThat(actual).isEmpty();
        verify(repo).findById(taskId);
    }

    @Test
    void create_whenTaskValid_returnsTask() {
        var executorId = 1;
        var taskId = 1L;
        var taskCreateDTO = TaskCreateDTO.builder().title("title").executorId(executorId).build();
        var expected = TaskGetDTO.builder().id(taskId).title(taskCreateDTO.getTitle()).executorId(taskCreateDTO.getExecutorId()).build();

        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Task task = invocation.getArgument(0);

            return TaskGetDTO.builder().id(task.getId()).title(task.getTitle()).executorId(task.getExecutor().getId()).build();
        });
        when(userRepo.findById(any())).thenAnswer(invocation -> Optional.of(User.builder().id(invocation.getArgument(0)).build()));
        when(repo.save(any())).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        var actual = service.create(taskCreateDTO);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(actual.getId()).isNotNull();
        verify(mapper).sourceToDestination(any());
        verify(userRepo).findById(executorId);
        verify(repo).save(any());
    }

    @Test
    void create_whenTaskNotValid_throwsException() {
        var executorId = 1;
        var taskCreateDTO = TaskCreateDTO.builder().title("title").executorId(executorId).build();

        when(userRepo.findById(executorId)).thenAnswer(invocation -> Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> service.create(taskCreateDTO));
        verify(mapper, never()).sourceToDestination(any());
        verify(userRepo).findById(executorId);
        verify(repo, never()).save(any());
    }

    @Test
    void update_whenTaskExists_returnsTask() {
        var taskId = 1L;
        var executorId = 1;
        var task = Task.builder().id(taskId).title("title1").executor(User.builder().id(executorId).build()).build();
        var taskUpdateDTO = TaskUpdateDTO.builder().title("title2").executorId(executorId).build();
        var expected = TaskGetDTO.builder().id(taskId).title(taskUpdateDTO.getTitle()).executorId(taskUpdateDTO.getExecutorId()).build();

        when(repo.findById(taskId)).thenReturn(Optional.of(task));
        when(userRepo.findById(any())).thenAnswer(invocation -> Optional.of(User.builder().id(invocation.getArgument(0)).build()));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);

            return TaskGetDTO.builder().id(saved.getId()).title(saved.getTitle()).executorId(saved.getExecutor().getId()).build();
        });

        var actual = service.update(taskId, taskUpdateDTO);

        Assertions.assertThat(actual).isEqualTo(expected);
        verify(mapper).sourceToDestination(any());
    }

    @Test
    void update_whenTaskNotExists_throwsException() {
        var taskId = 1L;
        var executorId = 1;
        var taskUpdateDTO = TaskUpdateDTO.builder().title("title").executorId(executorId).build();

        when(repo.findById(taskId)).thenReturn(Optional.empty());

        Assertions.assertThatExceptionOfType(EntityNotFoundException.class).isThrownBy(() -> service.update(taskId, taskUpdateDTO));
        verify(mapper, never()).sourceToDestination(any());
    }

    @Test
    void update_whenUserNotAdminAndIsExecutor_returnsHalfUpdatedTask() {
        var taskId = 1L;
        var executorId = 2;
        var user = User.builder().id(executorId).role(Role.USER).build();
        var task = Task.builder().id(taskId).title("title").status(Status.NEW).priority(Priority.MEDIUM)
                .executor(User.builder().id(executorId).build()).build();
        var taskUpdateDTO = TaskUpdateDTO.builder().status(Status.WAITING).priority(Priority.LOW).build();
        var expected = TaskGetDTO.builder().id(taskId).title(task.getTitle()).status(taskUpdateDTO.getStatus())
                .priority(task.getPriority()).executorId(task.getExecutor().getId()).build();

        when(userService.getCurrentUser()).thenReturn(user);
        when(repo.findById(taskId)).thenReturn(Optional.of(task));
        when(mapper.sourceToDestination(any())).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);

            return TaskGetDTO.builder().id(saved.getId()).title(saved.getTitle()).status(saved.getStatus())
                    .priority(saved.getPriority()).executorId(saved.getExecutor().getId()).build();
        });

        var actual = service.update(taskId, taskUpdateDTO);

        Assertions.assertThat(actual).isEqualTo(expected);
        verify(mapper).sourceToDestination(any());
    }

    @Test
    void delete() {
        var taskId = 1L;

        doNothing().when(repo).deleteById(taskId);

        service.delete(taskId);

        verify(repo).deleteById(taskId);
    }
}