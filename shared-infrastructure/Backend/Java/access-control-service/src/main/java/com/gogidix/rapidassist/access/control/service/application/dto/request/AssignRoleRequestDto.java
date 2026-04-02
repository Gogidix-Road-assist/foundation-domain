package com.gogidix.rapidassist.access.control.service.application.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO: AssignRoleRequestDto
 *
 * Request DTO for assigning roles to subjects.
 */
public record AssignRoleRequestDto(
        @NotBlank(message = "Subject ID is required")
        String subjectId,

        @NotBlank(message = "Role ID is required")
        String roleId
) {
}
