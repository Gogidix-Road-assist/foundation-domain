package com.gogidix.rapidassist.shared.cors.config.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Auto-configuration for shared CORS settings across all services.
 *
 * <p>This configuration eliminates code duplication by providing a centralized,
 * properties-based approach to CORS configuration that can be reused across
 * all services in the platform.</p>
 *
 * <p>Activation: Add the shared-cors-config dependency to your pom.xml</p>
 *
 * <p>Configuration prefix: gogidix.cors</p>
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "gogidix.cors", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(CorsProperties.class)
public class SharedCorsAutoConfiguration {

    /**
     * Creates a CorsFilter bean based on the configured properties.
     *
     * <p>This bean is automatically registered and applies CORS settings to all
     * matching path patterns defined in {@link CorsProperties#getPathPatterns()}</p>
     *
     * @param properties The CORS configuration properties
     * @return A configured CorsFilter bean
     */
    @Bean
    public CorsFilter gogidixCorsFilter(CorsProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Parse allowed origins from comma-separated string or environment variable
        String allowedOrigins = System.getenv().getOrDefault("ALLOWED_ORIGINS", properties.getAllowedOrigins());
        config.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Configure allowed methods
        config.setAllowedMethods(properties.getAllowedMethods());

        // Configure allowed headers
        config.setAllowedHeaders(properties.getAllowedHeaders());

        // Configure credentials support
        config.setAllowCredentials(properties.getAllowCredentials());

        // Configure max age for preflight requests
        config.setMaxAge(properties.getMaxAge());

        // Register CORS configuration for all specified path patterns
        for (String pattern : properties.getPathPatterns()) {
            source.registerCorsConfiguration(pattern, config);
        }

        return new CorsFilter(source);
    }
}
