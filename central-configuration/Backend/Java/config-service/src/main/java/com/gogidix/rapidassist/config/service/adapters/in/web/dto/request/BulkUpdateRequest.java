package com.gogidix.rapidassist.config.service.adapters.in.web.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * Request DTO for bulk updating multiple configurations.
 * Contains a list of configuration updates to apply atomically.
 */
@Schema(description = "Request to bulk update multiple configurations")
public record BulkUpdateRequest(

    @Schema(description = "Tenant identifier", example = "tenant-001", required = true)
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Schema(description = "List of configuration updates", required = true)
    @NotNull(message = "Updates list is required")
    @NotEmpty(message = "At least one update is required")
    @Valid
    List<ConfigurationUpdateRequest> updates,

    @Schema(description = "Reason for bulk update", example = "Performance tuning - increasing timeouts")
    String reason
) {}
