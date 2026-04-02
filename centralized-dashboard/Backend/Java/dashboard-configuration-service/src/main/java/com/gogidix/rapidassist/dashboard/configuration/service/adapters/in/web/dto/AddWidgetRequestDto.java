package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for adding a widget to a dashboard.
 *
 * @param widget    the widget to add (required)
 * @param updatedBy the user ID who added the widget (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to add a widget to a dashboard")
public record AddWidgetRequestDto(
    @Schema(description = "Widget to add", required = true)
    @NotNull(message = "Widget is required")
    @Valid
    DashboardConfiguration.DashboardWidget widget,

    @Schema(description = "User ID of updater", example = "user-123", required = true)
    @NotBlank(message = "Updated by is required")
    String updatedBy
) {
}
