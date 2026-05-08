package com.gogidix.rapidassist.access.control.service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

/**
 * DTO: GrantPermissionRequestDto
 *
 * Request DTO for granting permissions.
 */
public record GrantPermissionRequestDto(
        @NotBlank(message = "Subject ID is required")
        String subjectId,

        @NotBlank(message = "Subject type is required")
        @Pattern(regexp = "USER|SERVICE|ROLE", message = "Subject type must be USER, SERVICE, or ROLE")
        String subjectType,

        @NotBlank(message = "Resource is required")
        String resource,

        @NotBlank(message = "Action is required")
        String action,

        @NotNull(message = "Effect is required")
        @Pattern(regexp = "ALLOW|DENY", message = "Effect must be ALLOW or DENY")
        String effect,

        Instant validUntil,

        String condition
) {
}
