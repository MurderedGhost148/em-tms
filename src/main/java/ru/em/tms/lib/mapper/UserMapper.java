package ru.em.tms.lib.mapper;

import org.mapstruct.Mapper;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.user.UserGetDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserGetDTO sourceToDestination(User user);
}
