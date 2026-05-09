package com.gogidix.rapidassist.dashboard.configuration.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for web-related settings including CORS.
 *
 * <p>This configuration provides secure CORS settings to replace the overly permissive
 * @CrossOrigin(origins = "*") annotation. CORS origins can be configured via
 * environment variables for different environments (dev, test, prod).</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
public class WebConfig {

    private static final String ALLOWED_ORIGINS_DEFAULT = "http://localhost:3000,http://localhost:5173";
    private static final String ALLOWED_METHODS = "GET,POST,PUT,DELETE,PATCH,OPTIONS";
    private static final String ALLOWED_HEADERS = "*";
    private static final String EXPOSE_HEADERS = "Location,Authorization";
    private static final long MAX_AGE = 3600L; // 1 hour

    /**
     * Creates and configures the CORS filter for the application.
     *
     * <p>The allowed origins are read from the CORS_ALLOWED_ORIGINS environment variable.
     * If not specified, defaults to localhost development origins.</p>
     *
     * <p>Production environments should set explicit allowed origins via environment variable.</p>
     *
     * @return configured CORS filter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Get allowed origins from environment or use defaults
        String allowedOriginsEnv = System.getenv().get("CORS_ALLOWED_ORIGINS");
        List<String> allowedOrigins = allowedOriginsEnv != null && !allowedOriginsEnv.isEmpty()
            ? Arrays.asList(allowedOriginsEnv.split(","))
            : Arrays.asList(ALLOWED_ORIGINS_DEFAULT.split(","));

        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(Arrays.asList(ALLOWED_METHODS.split(",")));
        config.setAllowedHeaders(Arrays.asList(ALLOWED_HEADERS.split(",")));
        config.setExposedHeaders(Arrays.asList(EXPOSE_HEADERS.split(",")));
        config.setAllowCredentials(true);
        config.setMaxAge(MAX_AGE);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
