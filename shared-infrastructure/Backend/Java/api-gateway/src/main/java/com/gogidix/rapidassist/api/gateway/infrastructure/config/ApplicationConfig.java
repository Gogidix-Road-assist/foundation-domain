package com.gogidix.rapidassist.api.gateway.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * Configuration: ApplicationConfig
 *
 * Main application configuration for the API Gateway service.
 * This service uses Spring Cloud Gateway with WebFlux (reactive stack).
 *
 * Note: For tenant context propagation in reactive applications,
 * use Reactor Context instead of ThreadLocal or interceptors.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@Configuration
public class ApplicationConfig implements WebFluxConfigurer {

    /**
     * WebFlux configuration for the API Gateway.
     *
     * TODO: Add reactive filters for tenant context propagation once infrastructure/security/ReactiveTenantContextFilter.java is implemented
     */
    public ApplicationConfig() {
        // Reactive filters will be registered in Gateway routes
        // See: GatewayEdgeAutoConfiguration for route-level filters
    }
}
