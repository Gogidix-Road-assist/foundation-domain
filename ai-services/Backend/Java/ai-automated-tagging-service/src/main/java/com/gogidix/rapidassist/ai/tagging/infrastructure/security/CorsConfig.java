package com.gogidix.rapidassist.ai.tagging.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration for Multi-Tenant SaaS
 * Configures allowed origins for cross-origin requests
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS,PATCH}")
    private List<String> allowedMethods;

    @Value("${cors.allowed-headers:*}")
    private List<String> allowedHeaders;

    @Value("${cors.exposed-headers:X-Correlation-ID,X-Tenant-ID}")
    private List<String> exposedHeaders;

    @Value("${cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private Long maxAge;

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(allowCredentials);
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(allowedHeaders);
        config.setExposedHeaders(exposedHeaders);
        config.setMaxAge(maxAge);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
