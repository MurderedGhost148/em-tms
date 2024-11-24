package ru.em.tms.lib.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import ru.em.tms.model.db.User;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;
import ru.em.tms.repo.UserRepo;

import java.util.Objects;

public class TMSMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    private final TaskRepo taskRepo;
    private final CommentRepo commentRepo;
    private final UserRepo userRepo;

    public TMSMethodSecurityExpressionRoot(TaskRepo taskRepo, CommentRepo commentRepo, UserRepo userRepo, Authentication authentication) {
        super(authentication);

        this.taskRepo = taskRepo;
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    public boolean isTaskMember(Long taskId) {
        final User user = userRepo.findByEmail(((UserDetails) this.getPrincipal()).getUsername())
                .orElseThrow(() -> new AccessDeniedException("Отказано в доступе"));

        try {
            taskRepo.findById(taskId).ifPresentOrElse(t -> {
                if(!Objects.equals(t.getAuthor().getId(), user.getId()) && (t.getExecutor() == null
                        || !Objects.equals(t.getExecutor().getId(), user.getId()))) {
                    throw new AccessDeniedException("Отказано в доступе");
                }
            }, () -> {
                throw new AccessDeniedException("Отказано в доступе");
            });
        } catch (AccessDeniedException e) {
            return false;
        }

        return true;
    }

    public boolean isCommentAuthor(Long commentId) {
        final User user = userRepo.findByEmail(((UserDetails) this.getPrincipal()).getUsername())
                .orElseThrow(() -> new AccessDeniedException("Отказано в доступе"));

        try {
            commentRepo.findById(commentId).ifPresentOrElse(t -> {
                if(!Objects.equals(t.getAuthor().getId(), user.getId())) {
                    throw new AccessDeniedException("Отказано в доступе");
                }
            }, () -> {
                throw new AccessDeniedException("Отказано в доступе");
            });
        } catch (AccessDeniedException e) {
            return false;
        }

        return true;
    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }
}
