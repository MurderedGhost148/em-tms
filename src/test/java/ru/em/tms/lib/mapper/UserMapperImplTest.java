package ru.em.tms.lib.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.model.enums.Role;

class UserMapperImplTest {
    private final UserMapperImpl userMapperImpl = new UserMapperImpl();

    @Test
    void sourceToDestination_whenFullObject_returnsDTO() {
        var user = User.builder().id(1).password("12345zxC!").email("test@test.ru").role(Role.USER).build();
        var expected = new UserGetDTO(user.getId(), user.getEmail(), user.getRole());

        var actual = userMapperImpl.sourceToDestination(user);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void sourceToDestination_whenEmptyObject_returnsDTO() {
        var user = new User();
        var expected = new UserGetDTO(null, null, null);

        var actual = userMapperImpl.sourceToDestination(user);

        Assertions.assertEquals(expected, actual);
    }
}