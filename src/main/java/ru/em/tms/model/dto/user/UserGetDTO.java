package ru.em.tms.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.Role;

@Schema(description = "UserGet")
@Value
public class UserGetDTO implements IDTO {
    @Schema(description = "Идентификатор пользователя", example = "1")
    Integer id;
    @Schema(description = "Email", example = "test@test.ru")
    String email;
    @Schema(description = "Роль", example = "USER")
    Role role;
}
