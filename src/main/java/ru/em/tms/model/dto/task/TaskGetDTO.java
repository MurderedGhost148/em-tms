package ru.em.tms.model.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.task.Priority;
import ru.em.tms.model.enums.task.Status;

import java.time.LocalDateTime;

@Value
public class TaskGetDTO implements IDTO {
    Long id;
    String title;
    String description;
    Status status;
    Priority priority;
    @JsonProperty("author_id")
    Integer authorId;
    @JsonProperty("created_at")
    LocalDateTime createdAt;
    @JsonProperty("updated_at")
    LocalDateTime updatedAt;
    @JsonProperty("executor_id")
    Integer executorId;
}
