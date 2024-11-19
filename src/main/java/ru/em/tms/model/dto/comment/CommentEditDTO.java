package ru.em.tms.model.dto.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import ru.em.tms.model.dto.IDTO;

@Schema(description = "CommentEdit")
@Value
@Jacksonized
@Builder
public class CommentEditDTO implements IDTO {
    @Schema(description = "Комментарий", example = "Тест")
    @NotBlank(message = "Комментарий не может быть пустым")
    String content;
}
