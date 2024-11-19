package ru.em.tms.lib.security;

import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.EvaluationContext;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import ru.em.tms.repo.CommentRepo;
import ru.em.tms.repo.TaskRepo;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class TMSMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {
    private final AuthenticationTrustResolver trustResolver = new AuthenticationTrustResolverImpl();
    private final TaskRepo taskRepo;
    private final CommentRepo commentRepo;

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
            Authentication authentication, MethodInvocation invocation) {
        TMSMethodSecurityExpressionRoot root = new TMSMethodSecurityExpressionRoot(taskRepo, commentRepo, authentication);
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(this.trustResolver);
        root.setRoleHierarchy(getRoleHierarchy());
        return root;
    }

    @Override
    public EvaluationContext createEvaluationContext(Supplier<Authentication> authentication,
                                                     MethodInvocation invocation) {
        return createEvaluationContext(authentication.get(), invocation);
    }
}
