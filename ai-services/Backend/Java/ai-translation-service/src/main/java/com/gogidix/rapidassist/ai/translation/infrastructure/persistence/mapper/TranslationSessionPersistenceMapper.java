package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationSessionEntity;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationRequestEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between TranslationSession domain and entity.
 */
@Component
public class TranslationSessionPersistenceMapper {

    public TranslationSessionEntity toEntity(TranslationSession domain) {
        if (domain == null) {
            return null;
        }

        return TranslationSessionEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .sessionId(domain.getSessionId())
                .status(domain.getStatus())
                .sessionType(domain.getSessionType())
                .defaultSourceLanguage(domain.getDefaultSourceLanguage())
                .defaultTargetLanguage(domain.getDefaultTargetLanguage())
                .channel(domain.getChannel())
                .metadata(domain.getMetadata() != null ? domain.getMetadata().toString() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .lastActivityAt(domain.getLastActivityAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }

    public TranslationSession toDomain(TranslationSessionEntity entity) {
        if (entity == null) {
            return null;
        }

        return TranslationSession.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .sessionId(entity.getSessionId())
                .status(entity.getStatus())
                .sessionType(entity.getSessionType())
                .defaultSourceLanguage(entity.getDefaultSourceLanguage())
                .defaultTargetLanguage(entity.getDefaultTargetLanguage())
                .channel(entity.getChannel())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastActivityAt(entity.getLastActivityAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .translationRequests(new java.util.ArrayList<>())
                .build();
    }
}
