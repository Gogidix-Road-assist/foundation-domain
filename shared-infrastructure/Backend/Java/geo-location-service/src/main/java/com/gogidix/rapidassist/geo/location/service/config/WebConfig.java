package com.gogidix.rapidassist.geo.location.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for web-related settings including CORS.
 */
@Configuration
public class WebConfig {

    private static final String ALLOWED_ORIGINS_ENV = "ALLOWED_ORIGINS";
    private static final String DEFAULT_ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:8080";

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        String allowedOriginsEnv = System.getenv().getOrDefault(ALLOWED_ORIGINS_ENV, DEFAULT_ALLOWED_ORIGINS);
        List<String> allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
