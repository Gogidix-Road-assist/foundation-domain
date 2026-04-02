package com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto;

import com.gogidix.rapidassist.dashboard.configuration.service.domain.model.DashboardConfiguration;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for updating dashboard theme.
 *
 * @param theme     the new theme configuration (required)
 * @param updatedBy the user ID who updated the theme (required)
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Schema(description = "Request to update dashboard theme")
public record UpdateThemeRequestDto(
    @Schema(description = "New theme configuration", required = true)
    @NotNull(message = "Theme is required")
    @Valid
    DashboardConfiguration.DashboardTheme theme,

    @Schema(description = "User ID of updater", example = "user-123", required = true)
    @NotBlank(message = "Updated by is required")
    String updatedBy
) {
}
