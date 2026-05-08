package com.gogidix.rapidassist.config.service.application.dto.response;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;

import java.time.Instant;
import java.util.Set;

/**
 * Response DTO for Configuration entities.
 *
 * <p>This DTO is used for API responses and contains only the
 * information that should be exposed to clients.
 */
public record ConfigurationResponseDto(
    String id,
    String tenantId,
    String configKey,
    String environment,
    String namespace,
    Integer version,
    Object value,
    String dataType,
    boolean encrypted,
    boolean required,
    Object defaultValue,
    String description,
    Set<String> tags,
    String status,
    String createdBy,
    Instant createdAt,
    String updatedBy,
    Instant updatedAt,
    Instant lastValidatedAt
) {
    /**
     * Converts a domain Configuration entity to a Response DTO.
     *
     * @param configuration the domain entity
     * @return the response DTO
     */
    public static ConfigurationResponseDto fromDomain(Configuration configuration) {
        return new ConfigurationResponseDto(
            configuration.id(),
            configuration.tenantId(),
            configuration.configKey(),
            configuration.environment(),
            configuration.namespace(),
            configuration.version(),
            configuration.value(),
            configuration.dataType().name(),
            configuration.encrypted(),
            configuration.required(),
            configuration.defaultValue(),
            configuration.description(),
            configuration.tags(),
            configuration.status().name(),
            configuration.createdBy(),
            configuration.createdAt(),
            configuration.updatedBy(),
            configuration.updatedAt(),
            configuration.lastValidatedAt()
        );
    }
}
