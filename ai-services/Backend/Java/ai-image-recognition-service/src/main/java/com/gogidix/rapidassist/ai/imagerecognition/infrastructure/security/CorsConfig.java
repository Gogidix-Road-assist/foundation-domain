package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration.
 * Configures cross-origin resource sharing for the application.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Allowed origins from environment or defaults
        // In production, this should be configured via environment variables
        String allowedOrigins = System.getenv().getOrDefault("ALLOWED_ORIGINS",
                "http://localhost:3000,http://localhost:4200,https://rapidassist.gogidix.com");

        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Allowed headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Tenant-ID",
                "X-User-ID",
                "X-Correlation-ID",
                "X-Request-ID"
        ));

        // Allowed methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
                "X-Correlation-ID",
                "X-Request-ID",
                "X-Tenant-ID"
        ));

        // Max age for preflight requests (1 hour)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
