package ru.em.tms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import ru.em.tms.model.RestError;
import ru.em.tms.model.dto.JwtDTO;
import ru.em.tms.model.dto.user.UserAuthDTO;
import ru.em.tms.service.AuthService;

@RestController
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthService service;

    @Operation(summary = "Регистрация пользователя")
    @SecurityRequirements
    @PostMapping("/register")
    public JwtDTO signUp(@RequestBody @Valid UserAuthDTO request) {
        return JwtDTO.of(service.signUp(request));
    }

    @Operation(summary = "Авторизация пользователя")
    @SecurityRequirements
    @PostMapping("/login")
    public JwtDTO signIn(@RequestBody @Valid UserAuthDTO request) {
        return JwtDTO.of(service.signIn(request));
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public RestError badCredentials() {
        return new RestError("Неверный логин или пароль");
    }
}
