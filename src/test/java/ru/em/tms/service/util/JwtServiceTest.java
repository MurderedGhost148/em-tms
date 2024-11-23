package ru.em.tms.service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.em.tms.model.db.User;
import ru.em.tms.model.enums.Role;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        Field field = JwtService.class.getDeclaredField("jwtSigningKey");
        field.setAccessible(true);
        field.set(jwtService, "A5C26D3F7B2A48E6F1D4E0A753965F423D6F237E5C1B784E6A3A5F278D635B56");
    }

    @Test
    void generateToken_returnsToken() {
        String token = jwtService.generateToken(User.builder().id(1).email("test@test.ru").role(Role.ADMIN).build());
        assertNotNull(token);
    }

    @Test
    void isTokenValid_whenUserSame_returnsTrue() {
        var user = User.builder().id(1).email("test@test.ru").role(Role.ADMIN).build();
        String token = jwtService.generateToken(user);
        boolean result = jwtService.isTokenValid(token, user);
        assertTrue(result);
    }

    @Test
    void isTokenValid_whenUserNotSame_returnsFalse() {
        var user = User.builder().id(1).email("test1@test.ru").role(Role.ADMIN).build();
        String token = jwtService.generateToken(user);
        var user2 = User.builder().id(2).email("test2@test.ru").role(Role.ADMIN).build();
        boolean result = jwtService.isTokenValid(token, user2);
        assertFalse(result);
    }

    @Test
    void extractUserName_returnsEmail() {
        var user = User.builder().id(1).email("test@test.ru").role(Role.ADMIN).build();
        String token = jwtService.generateToken(user);
        String result = jwtService.extractUserName(token);
        assertEquals("test@test.ru", result);
    }
}