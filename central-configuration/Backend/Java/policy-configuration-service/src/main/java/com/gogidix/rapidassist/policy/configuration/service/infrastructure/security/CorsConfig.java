package com.gogidix.rapidassist.policy.configuration.service.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) Configuration
 * Configures allowed origins, methods, and headers for cross-origin requests
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String[] allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String[] allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String[] allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    /**
     * Configure CORS filter with environment-based settings
     *
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Set allowed origins from environment variable
        List<String> origins = Arrays.asList(allowedOrigins);
        config.setAllowedOrigins(origins);

        // Set allowed methods
        config.setAllowedMethods(Arrays.asList(allowedMethods));

        // Set allowed headers
        config.setAllowedHeaders(Arrays.asList(allowedHeaders));

        // Enable credentials (cookies, authorization headers)
        config.setAllowCredentials(allowCredentials);

        // Set preflight request cache duration (in seconds)
        config.setMaxAge(maxAge);

        // Apply CORS configuration to all paths
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
