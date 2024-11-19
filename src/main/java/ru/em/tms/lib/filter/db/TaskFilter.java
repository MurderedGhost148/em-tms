package ru.em.tms.lib.filter.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public class TaskFilter {
    @JsonProperty("author_id")
    Integer authorId;
    @JsonProperty("executor_id")
    Integer executorId;
}
