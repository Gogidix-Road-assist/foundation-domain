package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.access.control.service.domain.model.Subject;
import org.springframework.stereotype.Component;

/**
 * Converter: SubjectDocumentConverter
 *
 * Converts between Subject domain model and SubjectDocument.
 */
@Component
public class SubjectDocumentConverter {

    public SubjectDocument toDocument(Subject subject) {
        return new SubjectDocument(
                subject.getId(),
                subject.getTenantId(),
                subject.getSubjectType(),
                subject.getSubjectKey(),
                subject.getDisplayName(),
                new java.util.ArrayList<>(subject.getRoleIds()),
                subject.getCreatedAt(),
                subject.getLastAccessAt(),
                subject.isActive()
        );
    }

    public Subject toDomain(SubjectDocument document) {
        return Subject.builder()
                .id(document.getId())
                .tenantId(document.getTenantId())
                .subjectType(document.getSubjectType())
                .subjectKey(document.getSubjectKey())
                .displayName(document.getDisplayName())
                .roleIds(document.getRoleIds())
                .createdAt(document.getCreatedAt())
                .lastAccessAt(document.getLastAccessAt())
                .active(document.isActive())
                .build();
    }
}
