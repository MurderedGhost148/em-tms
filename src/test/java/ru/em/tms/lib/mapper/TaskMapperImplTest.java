package ru.em.tms.lib.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.task.TaskGetDTO;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

import java.time.LocalDateTime;

class TaskMapperImplTest {
    private final TaskMapperImpl taskMapperImpl = new TaskMapperImpl();

    @Test
    void sourceToDestination_whenFullObject_returnsDTO() {
        var task = Task.builder()
                .id(1L).title("test").description("test")
                .author(User.builder().id(1).build())
                .executor(User.builder().id(2).build())
                .priority(Priority.MEDIUM).status(Status.DONE)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
        var expected = TaskGetDTO.builder()
                .id(task.getId()).title(task.getTitle()).description(task.getDescription())
                .authorId(task.getAuthor().getId())
                .executorId(task.getExecutor().getId())
                .status(task.getStatus()).priority(task.getPriority())
                .createdAt(task.getCreatedAt()).updatedAt(task.getUpdatedAt())
                .build();

        var actual = taskMapperImpl.sourceToDestination(task);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void sourceToDestination_whenEmptyObject_returnsDTO() {
        var task = Task.builder()
                .id(1L).title("test")
                .author(User.builder().id(1).build())
                .build();
        var expected = TaskGetDTO.builder()
                .id(task.getId()).title(task.getTitle())
                .authorId(task.getAuthor().getId())
                .build();

        var actual = taskMapperImpl.sourceToDestination(task);

        Assertions.assertEquals(expected, actual);
    }
}