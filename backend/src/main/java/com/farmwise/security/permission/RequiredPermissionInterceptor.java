package com.farmwise.security.permission;

import com.farmwise.common.exception.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequiredPermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        RequiredPermission requiredPermission =
                handlerMethod.getMethodAnnotation(RequiredPermission.class);

        if (requiredPermission == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new BizException(HttpStatus.UNAUTHORIZED, "请先登录");
        }

        String requiredPermissionCode = requiredPermission.value();

        boolean hasPermission = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals(requiredPermissionCode));

        if (!hasPermission) {
            throw new BizException(HttpStatus.FORBIDDEN, "无权访问该接口");
        }

        return true;
    }
}
