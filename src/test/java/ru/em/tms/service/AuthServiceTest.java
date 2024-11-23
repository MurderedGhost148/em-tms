package ru.em.tms.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.user.UserAuthDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.service.util.JwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @InjectMocks
    private AuthService authService;

    @Test
    void signUp_whenUserNewAndCorrect_returnsToken() {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "correct");
        var userDetails = new User(1, request.getEmail(), request.getPassword(), Role.USER);
        var UserGetDto = new UserGetDTO(userDetails.getId(), request.getEmail(), userDetails.getRole());
        String token = "token";

        when(userService.create(any())).thenReturn(UserGetDto);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        String result = authService.signUp(request);

        assertEquals(token, result);

        verify(userService).create(any());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void signUp_whenUserAlreadyExists_throwsException() {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "correct");

        when(userService.create(any())).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThatThrownBy(() -> authService.signUp(request)).isInstanceOf(DataIntegrityViolationException.class);

        verify(userService).create(any());
    }

    @Test
    void signIn_whenCorrectAuthData_returnsToken() {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "correct");
        String token = "token";
        UserDetails userDetails = new User(1, request.getEmail(), request.getPassword(), Role.USER);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()))).thenReturn(authentication);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn(token);

        String result = authService.signIn(request);

        assertEquals(token, result);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService.userDetailsService()).loadUserByUsername(request.getEmail());
        verify(jwtService).generateToken(userDetails);
    }

    @Test
    void signIn_whenIncorrectAuthData_throwsException() {
        UserAuthDTO request = new UserAuthDTO("test@test.ru", "wrong");
        UserDetails userDetails = new User(1, request.getEmail(), request.getPassword(), Role.USER);

        when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),
                request.getPassword()))).thenThrow(BadCredentialsException.class);

        Assertions.assertThatThrownBy(() -> authService.signIn(request)).isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateToken(userDetails);
    }
}