package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Permission;
import org.springframework.stereotype.Component;

/**
 * Converter: PermissionDocumentConverter
 *
 * Converts between Permission domain model and PermissionDocument.
 */
@Component
public class PermissionDocumentConverter {

    public PermissionDocument toDocument(Permission permission) {
        return new PermissionDocument(
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
                permission.isActive()
        );
    }

    public Permission toDomain(PermissionDocument document) {
        return Permission.builder()
                .id(document.getId())
                .tenantId(document.getTenantId())
                .subjectId(document.getSubjectId())
                .subjectType(document.getSubjectType())
                .resource(document.getResource())
                .action(document.getAction())
                .effect(document.getEffect())
                .grantedAt(document.getGrantedAt())
                .grantedBy(document.getGrantedBy())
                .validUntil(document.getValidUntil())
                .condition(document.getCondition())
                .active(document.isActive())
                .build();
    }
}
