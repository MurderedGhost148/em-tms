package ru.em.tms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import ru.em.tms.config.TestSecurityConfig;
import ru.em.tms.model.dto.JwtDTO;
import ru.em.tms.model.dto.user.UserAuthDTO;
import ru.em.tms.service.AuthService;
import ru.em.tms.service.UserService;
import ru.em.tms.service.util.JwtService;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {
    @MockBean
    private AuthService authService;
    @MockBean
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        Field field = JwtService.class.getDeclaredField("jwtSigningKey");
        field.setAccessible(true);
        field.set(jwtService, "A5C26D3F7B2A48E6F1D4E0A753965F423D6F237E5C1B784E6A3A5F278D635B56");
    }

    @Test
    void signUp_whenValidRequest_shouldReturnJwtDTO() throws Exception {
        var token = "token";
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "12345zxC!");
        JwtDTO expectedResponse = JwtDTO.of(token);

        when(authService.signUp(request)).thenReturn(token);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").value(expectedResponse.getJwt()));

        verify(authService).signUp(any(UserAuthDTO.class));
    }

    @Test
    void signUp_whenInvalidRequest_shouldReturnError() throws Exception {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "12345zxC!");

        when(authService.signUp(request)).thenThrow(DataIntegrityViolationException.class);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").doesNotExist())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void signIn_whenValidRequest_shouldReturnJwtDTO() throws Exception {
        var token = "token";
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "12345zxC!");
        JwtDTO expectedResponse = JwtDTO.of(token);

        when(authService.signIn(request)).thenReturn(token);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").value(expectedResponse.getJwt()));

        verify(authService).signIn(any(UserAuthDTO.class));
    }

    @Test
    void signIn_whenInvalidRequest_shouldReturnError() throws Exception {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "12345zxC!");

        when(authService.signIn(request)).thenThrow(BadCredentialsException.class);

        mockMvc.perform(post("/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.jwt").doesNotExist())
                .andExpect(jsonPath("$.message").isNotEmpty());

        verify(authService).signIn(any(UserAuthDTO.class));
    }
}