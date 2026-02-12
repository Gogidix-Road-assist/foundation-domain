package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing dashboard.
 *
 * @param name        the new dashboard name (required, min 2 chars, max 100 chars)
 * @param description the new dashboard description (optional, max 500 chars)
 * @param updatedBy   the user ID who updated the dashboard (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to update an existing dashboard configuration")
public record UpdateDashboardRequestDto(
    @Schema(description = "Dashboard name", example = "Sales Dashboard (Updated)", required = true)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @Schema(description = "Dashboard description", example = "Updated overview of sales metrics")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @Schema(description = "User ID of updater", example = "user-123", required = true)
    @NotBlank(message = "Updated by is required")
    String updatedBy
) {
}
