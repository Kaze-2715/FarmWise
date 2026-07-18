package com.farmwise.security.config;

import com.farmwise.security.permission.RequiredPermissionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SecurityWebMvcConfig implements WebMvcConfigurer {

    private final RequiredPermissionInterceptor requiredPermissionInterceptor;

    public SecurityWebMvcConfig(
            RequiredPermissionInterceptor requiredPermissionInterceptor
    ) {
        this.requiredPermissionInterceptor = requiredPermissionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requiredPermissionInterceptor);
    }
}
