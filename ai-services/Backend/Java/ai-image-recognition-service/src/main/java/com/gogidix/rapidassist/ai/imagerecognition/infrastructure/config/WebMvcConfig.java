package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.config;

import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 * Registers interceptors for tenant context management.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/health",
                        "/actuator/info",
                        "/swagger-ui/**",
                        "/api-docs/**"
                );
    }
}
