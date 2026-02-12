package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Role;
import org.springframework.stereotype.Component;

/**
 * Converter: RoleDocumentConverter
 *
 * Converts between Role domain model and RoleDocument.
 */
@Component
public class RoleDocumentConverter {

    public RoleDocument toDocument(Role role) {
        return new RoleDocument(
                role.getId(),
                role.getTenantId(),
                role.getName(),
                role.getDescription(),
                new java.util.ArrayList<>(role.getPermissionIds()),
                role.getCreatedAt(),
                role.getCreatedBy(),
                role.getUpdatedAt(),
                role.getUpdatedBy(),
                role.isActive()
        );
    }

    public Role toDomain(RoleDocument document) {
        return Role.builder()
                .id(document.getId())
                .tenantId(document.getTenantId())
                .name(document.getName())
                .description(document.getDescription())
                .permissionIds(document.getPermissionIds())
                .createdAt(document.getCreatedAt())
                .createdBy(document.getCreatedBy())
                .updatedAt(document.getUpdatedAt())
                .updatedBy(document.getUpdatedBy())
                .active(document.isActive())
                .build();
    }
}
