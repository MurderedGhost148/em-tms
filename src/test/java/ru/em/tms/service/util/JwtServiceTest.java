package ru.em.tms.service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.em.tms.model.db.User;
import ru.em.tms.model.enums.Role;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;
    private static final String jwtSigningKey = "A5C26D3F7B2A48E6F1D4E0A753965F423D6F237E5C1B784E6A3A5F278D635B56";

    @BeforeEach
    void setUp() throws Exception {
        Field field = JwtService.class.getDeclaredField("jwtSigningKey");
        field.setAccessible(true);
        field.set(jwtService, jwtSigningKey);
    }

    @Test
    void generateToken_returnsToken() {
        String token = jwtService.generateToken(User.builder().id(1).email("test@test.ru").role(Role.ADMIN).build());
        assertNotNull(token);
    }

    @Test
    void generateToken_whenNotUserInstance_returnsTokenWithoutExtraClaims() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var user = new UserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of(new SimpleGrantedAuthority(Role.USER.name()));
            }

            @Override
            public String getPassword() {
                return "12345zxC!";
            }

            @Override
            public String getUsername() {
                return "test@test.ru";
            }
        };
        String token = jwtService.generateToken(user);

        Class<JwtService> clazz = JwtService.class;
        Method method = clazz.getDeclaredMethod("extractAllClaims", String.class);
        method.setAccessible(true);
        Claims claims = (Claims) method.invoke(jwtService, token);

        Assertions.assertAll(
                () -> Assertions.assertFalse(claims.containsKey("id")),
                () -> Assertions.assertFalse(claims.containsKey("email")),
                () -> Assertions.assertFalse(claims.containsKey("role"))
        );
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
    void isTokenValid_whenTokenExpired_returnsFalse() {
        var user = User.builder().id(1).email("test@test.ru").role(Role.ADMIN).build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());

        var delta = 1000L * 60 * 60 * 24;
        String token = Jwts.builder().claims(claims).subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis() - delta * 2))
                .expiration(new Date(System.currentTimeMillis() - delta))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSigningKey)), Jwts.SIG.HS256).compact();

        boolean result = jwtService.isTokenValid(token, user);

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