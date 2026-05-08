package com.gogidix.rapidassist.access.control.service.application.dto.response;

import java.time.Instant;
import java.util.List;

/**
 * DTO: PermissionResponseDto
 *
 * Response DTO for permission data.
 */
public record PermissionResponseDto(
        String id,
        String tenantId,
        String subjectId,
        String subjectType,
        String resource,
        String action,
        String effect,
        Instant grantedAt,
        String grantedBy,
        Instant validUntil,
        String condition,
        boolean active,
        boolean valid
) {
    public static PermissionResponseDto from(com.gogidix.rapidassist.access.control.service.domain.model.Permission permission) {
        return new PermissionResponseDto(
                permission.getId(),
                permission.getTenantId(),
                permission.getSubjectId(),
                permission.getSubjectType(),
                permission.getResource(),
                permission.getAction(),
                permission.getEffect(),
                permission.getGrantedAt(),
                permission.getGrantedBy(),
                permission.getValidUntil(),
                permission.getCondition(),
                permission.isActive(),
                permission.isValid()
        );
    }
}
