package com.gogidix.rapidassist.access.control.service.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO: CreateRoleRequestDto
 *
 * Request DTO for creating roles.
 */
public record CreateRoleRequestDto(
        @NotBlank(message = "Role name is required")
        @Size(min = 2, max = 100, message = "Role name must be between 2 and 100 characters")
        String name,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description
) {
}
