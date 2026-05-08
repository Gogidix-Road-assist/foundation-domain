package com.gogidix.rapidassist.service.health.monitor.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration: ApplicationConfig
 *
 * Main application configuration for the service health monitor service.
 * Registers interceptors and web configuration for multi-tenant SaaS architecture.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    /**
     * Configure interceptors for the application.
     * Currently configured for API path patterns with exclusions for actuator and OpenAPI endpoints.
     *
     * TODO: Add TenantInterceptor once infrastructure/security/TenantInterceptor.java is implemented
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Tenant interceptor will be registered here once implemented
        // registry.addInterceptor(tenantInterceptor)
        //         .addPathPatterns("/api/v1/**")
        //         .excludePathPatterns("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**");
    }
}
