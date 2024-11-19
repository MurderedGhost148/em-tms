package ru.em.tms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserAuthDTO;
import ru.em.tms.model.enums.Role;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String signUp(UserAuthDTO request) {
        userService.create(UserEditDTO.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build());

        var user = loadUser(request.getEmail());

        return jwtService.generateToken(user);
    }

    public String signIn(UserAuthDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        var user = loadUser(request.getEmail());

        return jwtService.generateToken(user);
    }

    public UserDetails loadUser(String email) {
        return userService
                .userDetailsService()
                .loadUserByUsername(email);
    }
}
