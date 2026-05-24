package com.gogidix.rapidassist.country.localization.config.service.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

/**
 * General application configuration for the country localization config service.
 *
 * <p>This class can be used for general Spring configuration beans
 * that don't fit into specific configuration categories.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Configure Jackson ObjectMapper for JSON serialization.
     *
     * <p>This configuration enables:
     * <ul>
     *   <li>JavaTimeModule for Instant/LocalDate serialization</li>
     *   <li>Private field access for immutable domain models</li>
     *   <li>Getter method detection for non-standard getters</li>
     * </ul>
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build()
                .registerModule(new JavaTimeModule())
                .setVisibility(
                        new com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std(
                                JsonAutoDetect.Visibility.DEFAULT
                        )
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.PUBLIC_ONLY)
                        .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                );
    }

    /**
     * Configure RestTemplate for external service calls.
     *
     * <p>This bean is used by the TenantServiceApiClient and other REST clients.
     * In test environment, this can be mocked to avoid actual HTTP calls.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    // Additional application-level configuration can be added here
}
