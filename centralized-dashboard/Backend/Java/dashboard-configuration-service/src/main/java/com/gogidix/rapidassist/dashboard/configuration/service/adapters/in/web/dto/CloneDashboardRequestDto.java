package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for cloning a dashboard.
 *
 * @param newDashboardId the ID for the cloned dashboard (required, min 2 chars, max 50 chars)
 * @param clonedBy       the user ID who initiated the clone (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to clone an existing dashboard")
public record CloneDashboardRequestDto(
    @Schema(description = "ID for the cloned dashboard", example = "dash-002", required = true)
    @NotBlank(message = "New dashboard ID is required")
    @Size(min = 2, max = 50, message = "Dashboard ID must be between 2 and 50 characters")
    String newDashboardId,

    @Schema(description = "User ID who initiated the clone", example = "user-123", required = true)
    @NotBlank(message = "Cloned by is required")
    String clonedBy
) {
}
