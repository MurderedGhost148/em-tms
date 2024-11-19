package ru.em.tms.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;

@Schema(description = "Auth")
@Value
public class UserAuthDTO implements IDTO {
    @Schema(description = "Email", example = "test@test.ru")
    @Email(message = "Неверный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @Schema(description = "Пароль", example = "87654321cxZ!")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
}
