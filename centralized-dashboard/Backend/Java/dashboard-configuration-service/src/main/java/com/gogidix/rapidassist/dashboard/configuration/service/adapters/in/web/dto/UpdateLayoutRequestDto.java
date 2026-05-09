package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for updating dashboard layout.
 *
 * @param layout    the new layout configuration (required)
 * @param updatedBy the user ID who updated the layout (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to update dashboard layout")
public record UpdateLayoutRequestDto(
    @Schema(description = "New layout configuration", required = true)
    DashboardConfiguration.DashboardLayout layout,

    @Schema(description = "User ID of updater", example = "user-123", required = true)
    @NotBlank(message = "Updated by is required")
    String updatedBy
) {
}
