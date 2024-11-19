package ru.em.tms.model.dto.user;

import lombok.Value;
import ru.em.tms.model.dto.IDTO;
import ru.em.tms.model.enums.Role;

@Value
public class UserGetDTO implements IDTO {
    Integer id;
    String email;
    Role role;
}
