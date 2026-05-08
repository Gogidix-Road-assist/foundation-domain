package com.gogidix.rapidassist.config.service.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating an existing configuration.
 * Contains only the fields that can be modified after creation.
 */
@Schema(description = "Request to update an existing configuration")
public record UpdateConfigurationRequest(

    @Schema(description = "New configuration value", example = "60000", required = true)
    @NotNull(message = "Configuration value is required")
    Object value,

    @Schema(description = "Reason for the update", example = "Increasing timeout for slow networks")
    String reason,

    @Schema(description = "Force update even if validation fails", example = "false")
    boolean forceUpdate
) {}
