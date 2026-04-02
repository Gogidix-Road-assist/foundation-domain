package com.gogidix.rapidassist.ai.analytics.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

/**
 * CORS Configuration for the analytics service
 * Allows cross-origin requests from configured origins
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Allowed origins (configure based on environment)
        // In production, these should come from application properties
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:3001",
                "https://*.rapidassist.com",
                "https://*.gogidix.com"
        ));

        // Allowed headers
        config.setAllowedHeaders(Collections.singletonList("*"));

        // Allowed methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Exposed headers
        config.setExposedHeaders(Arrays.asList(
                "X-Tenant-ID",
                "X-User-ID",
                "X-Correlation-ID",
                "Authorization"
        ));

        // Max age for preflight requests
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
