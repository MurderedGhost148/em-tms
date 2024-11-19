package ru.em.tms.model.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;

import java.time.LocalDateTime;

@Value
public class CommentGetDTO implements IDTO {
    Long id;
    @JsonProperty("task_id")
    Long taskId;
    String content;
    @JsonProperty("author_id")
    Integer authorId;
    @JsonProperty("created_at")
    LocalDateTime createdAt;
    @JsonProperty("updated_at")
    LocalDateTime updatedAt;
}
