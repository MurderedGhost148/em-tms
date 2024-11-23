package ru.em.tms.model.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;

import java.time.LocalDateTime;

@Schema(description = "CommentGet")
@Value
@Builder
public class CommentGetDTO implements IDTO {
    @Schema(description = "Идентификатор комментария", example = "1")
    Long id;
    @Schema(description = "Идентификатор задачи", example = "1")
    @JsonProperty("task_id")
    Long taskId;
    @Schema(description = "Комментарий", example = "Тест")
    String content;
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
}
