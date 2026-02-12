package com.gogidix.rapidassist.ai.chatbot.infrastructure.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS configuration
 * Configures allowed origins for cross-origin requests
 */
@Configuration
@Slf4j
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        log.info("Configuring CORS with origins: {}", allowedOrigins);

        CorsConfiguration configuration = new CorsConfiguration();

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(allowCredentials);

        // Allowed origins
        configuration.setAllowedOrigins(allowedOrigins);

        // Allowed methods
        configuration.setAllowedMethods(allowedMethods);

        // Allowed headers
        configuration.setAllowedHeaders(List.of(allowedHeaders.split(",")));

        // Exposed headers
        configuration.setExposedHeaders(Arrays.asList(
            "X-Correlation-ID",
            "X-Tenant-ID",
            "Authorization"
        ));

        // Max age for preflight requests
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * Get the configuration bean for use in SecurityConfig
     */
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(allowCredentials);
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(List.of(allowedHeaders.split(",")));
        configuration.setExposedHeaders(Arrays.asList(
            "X-Correlation-ID",
            "X-Tenant-ID",
            "Authorization"
        ));
        configuration.setMaxAge(maxAge);
        return configuration;
    }
}
