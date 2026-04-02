package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Jackson configuration for JSON serialization.
 *
 * <p>This configuration enables:
 * <ul>
 *   <li>JavaTimeModule for Instant/LocalDate serialization</li>
 *   <li>Field visibility for immutable domain models with @JsonAutoDetect</li>
 * </ul>
 */
@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .findAndAddModules()
                .build();
    }
}
