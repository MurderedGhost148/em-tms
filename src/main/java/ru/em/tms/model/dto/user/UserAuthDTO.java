package ru.em.tms.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;

@Value
public class UserAuthDTO implements IDTO {
    @Email(message = "Неверный email")
    @NotBlank(message = "Email не может быть пустым")
    String email;
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    String password;
}
