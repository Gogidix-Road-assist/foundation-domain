package com.gogidix.rapidassist.database.management.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for web-related settings including CORS.
 * <p>
 * This configuration provides a secure CORS setup that can be customized
 * through environment variables. By default, it allows origins from the
 * same domain and can be extended to include specific whitelisted domains.
 * </p>
 */
@Configuration
public class WebConfig {

    private static final String ALLOWED_ORIGINS_ENV = "ALLOWED_ORIGINS";
    private static final String DEFAULT_ALLOWED_ORIGINS = "http://localhost:3000,http://localhost:8080";

    /**
     * Creates a CORS filter with configurable allowed origins.
     * <p>
     * The allowed origins can be configured via the ALLOWED_ORIGINS environment variable
     * as a comma-separated list of URLs. If not specified, defaults to localhost development URLs.
     * </p>
     *
     * @return configured CORS filter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Get allowed origins from environment or use defaults
        String allowedOriginsEnv = System.getenv().getOrDefault(ALLOWED_ORIGINS_ENV, DEFAULT_ALLOWED_ORIGINS);
        List<String> allowedOrigins = Arrays.asList(allowedOriginsEnv.split(","));

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(allowedOrigins);
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L); // 1 hour

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
