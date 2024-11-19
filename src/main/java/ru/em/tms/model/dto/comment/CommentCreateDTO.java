package ru.em.tms.model.dto.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;

@Value
public class CommentCreateDTO implements IDTO {
    @NotBlank(message = "Комментарий не может быть пустым")
    String content;
    @JsonProperty("author_id")
    @NotNull(message = "Автор должен быть указан")
    Integer authorId;
}
