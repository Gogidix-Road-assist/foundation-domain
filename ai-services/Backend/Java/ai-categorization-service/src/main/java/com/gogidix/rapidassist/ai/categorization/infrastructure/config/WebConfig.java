package com.gogidix.rapidassist.ai.categorization.infrastructure.config;

import com.gogidix.rapidassist.ai.categorization.infrastructure.tenant.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC Configuration.
 *
 * This configuration:
 * - Registers the TenantInterceptor for ALL requests
 * - Ensures tenant context is set before any controller logic
 *
 * CRITICAL: TenantInterceptor MUST be registered here to ensure
 * tenant isolation for all incoming requests.
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TenantInterceptor tenantInterceptor;

    /**
     * Register interceptors.
     *
     * @param registry the interceptor registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register TenantInterceptor for ALL paths
        // This ensures tenant context is always set
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/actuator/health",
                        "/error",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}
