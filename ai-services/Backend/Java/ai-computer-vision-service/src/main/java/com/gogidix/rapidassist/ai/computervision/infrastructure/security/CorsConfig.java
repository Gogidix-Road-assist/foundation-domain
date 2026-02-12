package com.gogidix.rapidassist.ai.computervision.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for the AI Computer Vision Service
 * Configures cross-origin resource sharing for frontend access
 */
@Configuration
public class CorsConfig {

    private static final String ALLOWED_ORIGINS = System.getenv("ALLOWED_ORIGINS");

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials
        config.setAllowCredentials(true);

        // Configure allowed origins
        if (ALLOWED_ORIGINS != null && !ALLOWED_ORIGINS.isEmpty()) {
            config.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGINS.split(",")));
        } else {
            // Default origins for development
            config.setAllowedOriginPatterns(List.of("*"));
        }

        // Allow common headers
        config.addAllowedHeader("*");

        // Allow common methods
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("PATCH");
        config.addAllowedMethod("OPTIONS");

        // Expose headers needed for tenant context
        config.addExposedHeader("X-Correlation-ID");
        config.addExposedHeader("X-Tenant-ID");

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
