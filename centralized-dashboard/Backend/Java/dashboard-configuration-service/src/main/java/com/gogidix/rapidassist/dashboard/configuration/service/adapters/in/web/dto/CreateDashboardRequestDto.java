package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating a new dashboard.
 *
 * @param tenantId    the tenant ID (required)
 * @param dashboardId the dashboard ID (required, min 2 chars, max 50 chars)
 * @param name        the dashboard name (required, min 2 chars, max 100 chars)
 * @param description the dashboard description (optional, max 500 chars)
 * @param createdBy   the user ID who created the dashboard (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to create a new dashboard configuration")
public record CreateDashboardRequestDto(
    @Schema(description = "Tenant ID", example = "tenant-001", required = true)
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Schema(description = "Dashboard ID", example = "dash-001", required = true)
    @NotBlank(message = "Dashboard ID is required")
    @Size(min = 2, max = 50, message = "Dashboard ID must be between 2 and 50 characters")
    String dashboardId,

    @Schema(description = "Dashboard name", example = "Sales Dashboard", required = true)
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,

    @Schema(description = "Dashboard description", example = "Overview of sales metrics")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    String description,

    @Schema(description = "User ID of creator", example = "user-123", required = true)
    @NotBlank(message = "Created by is required")
    String createdBy
) {
}
