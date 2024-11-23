package ru.em.tms.model.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

@Schema(description = "TaskUpdate")
@Value
@Builder
public class TaskUpdateDTO implements IDTO {
    @Schema(description = "Заголовок", example = "Тест")
    @NotBlank(message = "Заголовок не может быть пустым")
    String title;
    @Schema(description = "Описание", example = "Тест")
    String description;
    @Schema(description = "Статус", example = "NEW")
    @NotNull(message = "Статус должен быть выбран")
    Status status;
    @Schema(description = "Приоритет", example = "LOW")
    @NotNull(message = "Приоритет должен быть выбран")
    Priority priority;
    @Schema(description = "Исполнитель", example = "1")
    @JsonProperty("executor_id")
    @NotNull(message = "Исполнитель должен быть выбран")
    Integer executorId;
}
