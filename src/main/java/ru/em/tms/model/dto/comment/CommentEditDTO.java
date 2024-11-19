package ru.em.tms.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.em.tms.model.dto.IDTO;

@Value
@Jacksonized
@Builder
public class CommentEditDTO implements IDTO {
    @NotBlank(message = "Комментарий не может быть пустым")
    String content;
}
