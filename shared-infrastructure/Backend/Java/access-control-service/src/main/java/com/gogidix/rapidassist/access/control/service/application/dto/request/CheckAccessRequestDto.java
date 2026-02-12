package com.gogidix.rapidassist.access.control.service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO: CheckAccessRequestDto
 *
 * Request DTO for access control checks.
 */
public record CheckAccessRequestDto(
        @NotBlank(message = "Subject ID is required")
        String subjectId,

        @NotBlank(message = "Resource is required")
        String resource,

        @NotBlank(message = "Action is required")
        String action,

        Map<String, Object> context
) {
    public CheckAccessRequestDto {
        if (context == null) {
            context = Map.of();
        }
    }
}
