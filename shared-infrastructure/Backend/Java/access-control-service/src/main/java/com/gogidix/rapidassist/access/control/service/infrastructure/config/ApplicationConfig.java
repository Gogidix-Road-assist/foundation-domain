package com.gogidix.rapidassist.access.control.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration: ApplicationConfig
 *
 * Main application configuration for the access-control service.
 * Registers interceptors and web configuration.
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    private final com.gogidix.rapidassist.access.control.service.infrastructure.security.TenantInterceptor tenantInterceptor;

    public ApplicationConfig(com.gogidix.rapidassist.access.control.service.infrastructure.security.TenantInterceptor tenantInterceptor) {
        this.tenantInterceptor = tenantInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
    }
}
