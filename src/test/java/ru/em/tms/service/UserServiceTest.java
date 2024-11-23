package ru.em.tms.service;

import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.em.tms.lib.mapper.UserMapper;
import ru.em.tms.model.db.User;
import ru.em.tms.model.dto.user.UserEditDTO;
import ru.em.tms.model.dto.user.UserGetDTO;
import ru.em.tms.model.enums.Role;
import ru.em.tms.repo.UserRepo;

import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepo repo;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserService service;

    @Test
    void getAll_whenAccessiblePageableParams_returnsAll() {
        var pageable = PageRequest.of(0, 10);
        var users = new LinkedList<User>(){{
            for(int i = 0; i < 10; i++) add(new User());
        }};
        var pageExcepted = new PageImpl<>(users, pageable, users.size() + 1);

        when(repo.findAll(pageable)).thenReturn(pageExcepted);

        var pageActual = service.getAll(pageable);

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(pageExcepted.getContent().size())
        );
    }

    @Test
    void getAll_whenNotAccessiblePageableParams_returnsNone() {
        var pageable = PageRequest.of(1, 10);
        var users = new LinkedList<User>();
        var pageExcepted = new PageImpl<>(users, pageable, 0);

        when(repo.findAll(pageable)).thenReturn(pageExcepted);

        var pageActual = service.getAll(pageable);

        assertAll(
                () -> Assertions.assertThat(pageActual.getTotalPages()).isEqualTo(pageExcepted.getTotalPages()),
                () -> Assertions.assertThat(pageActual.getPage()).isEqualTo(pageExcepted.getPageable().getPageNumber()),
                () -> Assertions.assertThat(pageActual.getSize()).isEqualTo(pageExcepted.getPageable().getPageSize()),
                () -> Assertions.assertThat(pageActual.getResult().size()).isEqualTo(pageExcepted.getContent().size())
        );
        verify(repo).findAll(pageable);
    }

    @Test
    void getById_whenUserExists_returnsUser() {
        var user = new User();

        when(repo.findById(1)).thenReturn(Optional.of(user));
        when(mapper.sourceToDestination(user)).thenReturn(new UserGetDTO(user.getId(), user.getEmail(), user.getRole()));

        var actual = service.getById(1);

        Assertions.assertThat(actual).isPresent();
        Assertions.assertThat(actual.get()).isEqualTo(new UserGetDTO(user.getId(), user.getEmail(), user.getRole()));
        verify(repo).findById(1);
    }

    @Test
    void getById_whenUserNotExists_returnsNone() {
        var id = 1;
        when(repo.findById(id)).thenReturn(Optional.empty());

        var actual = service.getById(id);

        Assertions.assertThat(actual).isEmpty();
        verify(repo).findById(id);
    }

    @Test
    void create_whenUserValid_returnsUser() {
        var user = new User();
        var userEditDto = UserEditDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();
        var userExpected = new UserGetDTO(user.getId(), user.getEmail(), user.getRole());

        when(repo.save(user)).thenReturn(user);
        when(mapper.sourceToDestination(user)).thenReturn(userExpected);

        var actual = service.create(userEditDto);

        Assertions.assertThat(actual).isEqualTo(userExpected);
        verify(repo).save(user);
    }

    @Test
    void create_whenUserAlreadyExists_throwsException() {
        var user = new User();
        var userEditDto = UserEditDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();

        when(repo.save(user)).thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThatThrownBy(() -> service.create(userEditDto)).isInstanceOf(DataIntegrityViolationException.class);
        verify(repo).save(user);
    }

    @Test
    void update_whenUserValid_returnsUser() {
        var user = new User(1, "test@test.ru", "password", Role.USER);
        var userEditDto = UserEditDTO.builder()
                .email(user.getEmail())
                .password("newPassword")
                .role(user.getRole())
                .build();
        var updatedUser = new UserGetDTO(user.getId(), user.getEmail(), user.getRole());

        when(repo.findById(1)).thenReturn(Optional.of(user));
        when(mapper.sourceToDestination(user))
                .thenAnswer(invocation -> new UserGetDTO(user.getId(), user.getEmail(), user.getRole()));

        var actual = service.update(1, userEditDto);

        Assertions.assertThat(actual).isEqualTo(updatedUser);
    }

    @Test
    void update_whenUserNotExists_throwsException() {
        var user = new User();
        var userEditDto = UserEditDTO.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .role(user.getRole())
                .build();

        when(repo.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.update(1, userEditDto)).isInstanceOf(EntityNotFoundException.class);
        verify(repo).findById(1);
    }

    @Test
    void delete_whenNotCurrentUser() {
        var userId = 1;
        var currentUser = new User(2, "test2@test.ru", "password", Role.ADMIN);

        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(repo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        service.delete(userId);

        verify(repo).deleteById(userId);
    }

    @Test
    void delete_whenCurrentUser() {
        var userId = 1;
        var currentUser = new User(userId, "test1@test.ru", "password", Role.ADMIN);

        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(currentUser.getEmail());
        when(repo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));

        Assertions.assertThatThrownBy(() -> service.delete(userId)).isInstanceOf(AccessDeniedException.class);
        verify(repo, never()).deleteById(userId);
    }

    @Test
    void getByEmail_whenUserExists_returnsUser() {
        var email = "test@test.ru";
        var excepted = User.builder().email(email).build();

        when(repo.findByEmail(email)).thenReturn(Optional.of(excepted));

        var actual = service.getByEmail(email);

        Assertions.assertThat(actual).isEqualTo(excepted);
        verify(repo).findByEmail(email);
    }

    @Test
    void getByEmail_whenUserNotExists_throwsException() {
        var email = "test@test.ru";

        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.getByEmail(email)).isInstanceOf(UsernameNotFoundException.class);
        verify(repo).findByEmail(email);
    }

    @Test
    void getCurrentUser_whenUserExists_returnsUser() {
        var email = "test@test.ru";
        var excepted = User.builder().email(email).build();

        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(repo.findByEmail(email)).thenReturn(Optional.of(excepted));

        var actual = service.getCurrentUser();

        Assertions.assertThat(actual).isEqualTo(excepted);
        verify(repo).findByEmail(email);
    }

    @Test
    void getCurrentUser_whenUserNotExists_throwsException() {
        var email = "test@test.ru";

        var authentication = mock(Authentication.class);
        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn(email);
        when(repo.findByEmail(email)).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> service.getCurrentUser()).isInstanceOf(UsernameNotFoundException.class);
        verify(repo).findByEmail(email);
    }
}