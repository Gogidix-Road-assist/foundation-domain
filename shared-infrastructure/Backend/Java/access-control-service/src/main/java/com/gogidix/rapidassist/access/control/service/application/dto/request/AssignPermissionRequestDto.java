package com.gogidix.rapidassist.access.control.service.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO: AssignPermissionRequestDto
 *
 * Request DTO for assigning permissions to roles.
 */
public record AssignPermissionRequestDto(
        @NotBlank(message = "Permission ID is required")
        String permissionId
) {
}
