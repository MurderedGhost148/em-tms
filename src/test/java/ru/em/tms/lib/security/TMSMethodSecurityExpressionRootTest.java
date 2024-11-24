package ru.em.tms.lib.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.em.tms.model.db.Comment;
import ru.em.tms.model.db.Task;
import ru.em.tms.model.db.User;
import ru.em.tms.model.enums.Role;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TMSMethodSecurityExpressionRootTest {
    @Mock
    private TaskRepo taskRepo;
    @Mock
    private CommentRepo commentRepo;
    @Mock
    private UserRepo userRepo;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private TMSMethodSecurityExpressionRoot root;

    @Test
    void isTaskMember_whenUserIsExecutor_returnsTrue() {
        var taskId = 1L;
        var currentUser = User.builder().id(1).email("admin@test.ru").role(Role.USER).build();
        var task = Task.builder().id(taskId).executor(User.builder().id(3).build())
                .author(currentUser).build();

        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(root.getPrincipal()).thenReturn(currentUser);
        when(taskRepo.findById(taskId)).thenReturn(Optional.ofNullable(task));

        var result = root.isTaskMember(taskId);

        Assertions.assertTrue(result);
    }

    @Test
    void isTaskMember_whenUserIsAuthor_returnsTrue() {
        var taskId = 1L;
        var currentUser = User.builder().id(3).email("admin@test.ru").role(Role.USER).build();
        var task = Task.builder().id(taskId).executor(currentUser)
                .author(User.builder().id(1).build()).build();

        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(root.getPrincipal()).thenReturn(currentUser);
        when(taskRepo.findById(taskId)).thenReturn(Optional.ofNullable(task));

        var result = root.isTaskMember(taskId);

        Assertions.assertTrue(result);
    }

    @Test
    void isTaskMember_whenNotTaskMember_returnsFalse() {
        var taskId = 1L;
        var currentUser = User.builder().id(4).email("admin@test.ru").role(Role.USER).build();
        var task = Task.builder().id(taskId).executor(User.builder().id(3).build())
                .author(User.builder().id(1).build()).build();

        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(root.getPrincipal()).thenReturn(currentUser);
        when(taskRepo.findById(taskId)).thenReturn(Optional.ofNullable(task));

        var result = root.isTaskMember(taskId);

        Assertions.assertFalse(result);
    }

    @Test
    void isCommentAuthor_whenUserIsAuthor_returnsTrue() {
        var commentId = 1L;
        var currentUser = User.builder().id(1).email("admin@test.ru").role(Role.USER).build();
        var comment = Comment.builder().id(commentId).task(Task.builder().id(1L).build())
                .author(User.builder().id(1).build()).build();

        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(root.getPrincipal()).thenReturn(currentUser);
        when(commentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        var result = root.isCommentAuthor(commentId);

        Assertions.assertTrue(result);
    }

    @Test
    void isCommentAuthor_whenNotCommentAuthor_returnsFalse() {
        var commentId = 1L;
        var currentUser = User.builder().id(1).email("admin@test.ru").role(Role.USER).build();
        var comment = Comment.builder().id(commentId).task(Task.builder().id(1L).build())
                .author(User.builder().id(2).build()).build();

        var securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(currentUser);
        when(userRepo.findByEmail(currentUser.getEmail())).thenReturn(Optional.of(currentUser));
        when(root.getPrincipal()).thenReturn(currentUser);
        when(commentRepo.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        var result = root.isCommentAuthor(commentId);

        Assertions.assertFalse(result);
    }
}