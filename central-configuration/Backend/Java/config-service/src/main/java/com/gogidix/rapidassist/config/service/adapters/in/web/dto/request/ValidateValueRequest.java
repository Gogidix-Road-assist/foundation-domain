package com.gogidix.rapidassist.config.service.adapters.in.web.dto.request;

import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for validating a configuration value against a schema.
 * Used for pre-validation before creating or updating configurations.
 */
@Schema(description = "Request to validate a configuration value")
public record ValidateValueRequest(

    @Schema(description = "Value to validate", example = "60000", required = true)
    @NotNull(message = "Value is required")
    Object value,

    @Schema(description = "Schema to validate against", required = true)
    @NotNull(message = "Schema is required")
    ConfigurationSchema schema
) {}
