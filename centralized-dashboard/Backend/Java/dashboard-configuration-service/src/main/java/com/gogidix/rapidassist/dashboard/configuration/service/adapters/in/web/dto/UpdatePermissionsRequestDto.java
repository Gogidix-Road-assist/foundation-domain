package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating dashboard permissions.
 *
 * @param permissions the new permissions configuration (required)
 * @param updatedBy   the user ID who updated the permissions (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to update dashboard permissions")
public record UpdatePermissionsRequestDto(
    @Schema(description = "New permissions configuration", required = true)
    @NotNull(message = "Permissions are required")
    @Valid
    DashboardConfiguration.DashboardPermissions permissions,

    @Schema(description = "User ID of updater", example = "user-123", required = true)
    @NotBlank(message = "Updated by is required")
    String updatedBy
) {
}
