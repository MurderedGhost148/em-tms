package ru.em.tms.model.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

@Value
public class TaskUpdateDTO implements IDTO {
    @NotBlank(message = "Заголовок не может быть пустым")
    String title;
    String description;
    @NotNull(message = "Статус должен быть выбран")
    Status status;
    @NotNull(message = "Приоритет должен быть выбран")
    Priority priority;
    @JsonProperty("executor_id")
    @NotNull(message = "Исполнитель должен быть выбран")
    Integer executorId;
}
