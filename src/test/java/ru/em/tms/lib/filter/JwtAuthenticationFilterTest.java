package ru.em.tms.lib.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import ru.em.tms.service.UserService;
import ru.em.tms.service.util.JwtService;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private UserService userService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private UserDetails userDetails;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void doFilterInternal_whenAuthHeaderIsEmpty_shouldNotAuthenticate() throws ServletException, IOException {
        when(request.getHeader(JwtAuthenticationFilter.HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_whenAuthHeaderInvalid_shouldNotAuthenticate() throws ServletException, IOException {
        when(request.getHeader(JwtAuthenticationFilter.HEADER)).thenReturn("invalid");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userService);
    }

    @Test
    void doFilterInternal_whenTokenValid_shouldAuthenticate() throws ServletException, IOException {
        String username = "test@test.ru";
        String token = "valid";
        when(request.getHeader(JwtAuthenticationFilter.HEADER)).thenReturn(JwtAuthenticationFilter.PREFIX + token);
        when(jwtService.extractUserName(token)).thenReturn(username);
        when(jwtService.isTokenValid(eq(token), any(UserDetails.class))).thenReturn(true);
        when(userService.userDetailsService()).thenReturn(userDetailsService);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }

    @Test
    void doFilterInternal_whenTokenInvalid_shouldNotAuthenticate() throws ServletException, IOException {
        String token = "invalid";
        when(request.getHeader(JwtAuthenticationFilter.HEADER)).thenReturn(JwtAuthenticationFilter.PREFIX + token);
        when(jwtService.extractUserName(token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}