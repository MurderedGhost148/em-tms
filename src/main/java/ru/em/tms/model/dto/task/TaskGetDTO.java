package ru.em.tms.model.dto.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

import java.time.LocalDateTime;

@Schema(description = "TaskGet")
@Value
@Builder
public class TaskGetDTO implements IDTO {
    @Schema(description = "Идентификатор задачи", example = "1")
    Long id;
    @Schema(description = "Заголовок", example = "Тест")
    String title;
    @Schema(description = "Описание", example = "Тест")
    String description;
    @Schema(description = "Статус", example = "NEW")
    Status status;
    @Schema(description = "Приоритет", example = "LOW")
    Priority priority;
    @Schema(description = "Автор", example = "1")
    @JsonProperty("author_id")
    Integer authorId;
    @Schema(description = "Дата создания", example = "2024-11-19 00:00:00")
    @JsonProperty("created_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdAt;
    @Schema(description = "Дата обновления", example = "2024-11-19 23:59:59")
    @JsonProperty("updated_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updatedAt;
    @Schema(description = "Исполнитель", example = "1")
    @JsonProperty("executor_id")
    Integer executorId;
}
