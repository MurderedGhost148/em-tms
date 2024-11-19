package ru.em.tms.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.Role;

@Schema(description = "UserEdit")
@Value
@Builder
public class UserEditDTO implements IDTO {
    @Schema(description = "Email", example = "test@test.ru")
    @Email(message = "Неверный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @Schema(description = "Пароль", example = "87654321cxZ!")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
    @Schema(description = "Роль", example = "USER")
    @NotNull(message = "Роль должна быть выбрана")
    Role role;
}
