package com.gogidix.rapidassist.config.service.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Individual configuration update request used in bulk operations.
 * Specifies the configuration key and its new value.
 */
@Schema(description = "Individual configuration update")
public record ConfigurationUpdateRequest(

    @Schema(description = "Configuration key", example = "api.timeout", required = true)
    @NotBlank(message = "Configuration key is required")
    String configKey,

    @Schema(description = "Environment", example = "production", required = true)
    @NotBlank(message = "Environment is required")
    String environment,

    @Schema(description = "Namespace", example = "api-gateway", required = true)
    @NotBlank(message = "Namespace is required")
    String namespace,

    @Schema(description = "New configuration value", example = "60000", required = true)
    @NotNull(message = "Configuration value is required")
    Object value
) {}
