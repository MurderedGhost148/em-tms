package ru.em.tms.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.Role;

@Value
public class UserEditDTO implements IDTO {
    @Email(message = "Неверный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
    @NotNull(message = "Роль должна быть выбрана")
    Role role; //todo: можно ли менять?
}
