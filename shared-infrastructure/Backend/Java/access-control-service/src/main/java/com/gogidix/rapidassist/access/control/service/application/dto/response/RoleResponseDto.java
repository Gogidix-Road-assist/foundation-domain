package com.gogidix.rapidassist.access.control.service.application.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * DTO: RoleResponseDto
 *
 * Response DTO for role data.
 */
public record RoleResponseDto(
        String id,
        String tenantId,
        String name,
        String description,
        List<String> permissionIds,
        int permissionCount,
        Instant createdAt,
        String createdBy,
        Instant updatedAt,
        boolean active
) {
    public static RoleResponseDto from(com.gogidix.rapidassist.access.control.service.domain.model.Role role) {
        return new RoleResponseDto(
                role.getId(),
                role.getTenantId(),
                role.getName(),
                role.getDescription(),
                role.getPermissionIds(),
                role.getPermissionIds().size(),
                role.getCreatedAt(),
                role.getCreatedBy(),
                role.getUpdatedAt(),
                role.isActive()
        );
    }

    public static RoleResponseDto from(com.gogidix.rapidassist.access.control.service.domain.aggregate.RoleAggregate aggregate) {
        com.gogidix.rapidassist.access.control.service.domain.model.Role role = aggregate.getRole();
        return new RoleResponseDto(
                role.getId(),
                role.getTenantId(),
                role.getName(),
                role.getDescription(),
                role.getPermissionIds(),
                aggregate.getPermissions().size(),
                role.getCreatedAt(),
                role.getCreatedBy(),
                role.getUpdatedAt(),
                role.isActive()
        );
    }
}
