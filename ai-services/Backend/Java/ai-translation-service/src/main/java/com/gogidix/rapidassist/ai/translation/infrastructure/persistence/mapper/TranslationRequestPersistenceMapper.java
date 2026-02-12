package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationRequestEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between TranslationRequest domain and entity.
 */
@Component
public class TranslationRequestPersistenceMapper {

    public TranslationRequestEntity toEntity(TranslationRequest domain) {
        if (domain == null) {
            return null;
        }

        return TranslationRequestEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .translationSessionId(domain.getTranslationSessionId())
                .sourceText(domain.getSourceText())
                .sourceLanguage(domain.getSourceLanguage())
                .targetLanguage(domain.getTargetLanguage())
                .translatedText(domain.getTranslatedText())
                .status(domain.getStatus())
                .detectedLanguage(domain.getDetectedLanguage())
                .detectionConfidence(domain.getDetectionConfidence())
                .processingTimeMs(domain.getProcessingTimeMs())
                .fromCache(domain.getFromCache())
                .cacheKey(domain.getCacheKey())
                .modelVersion(domain.getModelVersion())
                .metadata(domain.getMetadata() != null ? domain.getMetadata().toString() : null)
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .completedAt(domain.getCompletedAt())
                .build();
    }

    public TranslationRequest toDomain(TranslationRequestEntity entity) {
        if (entity == null) {
            return null;
        }

        return TranslationRequest.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .translationSessionId(entity.getTranslationSessionId())
                .sourceText(entity.getSourceText())
                .sourceLanguage(entity.getSourceLanguage())
                .targetLanguage(entity.getTargetLanguage())
                .translatedText(entity.getTranslatedText())
                .status(entity.getStatus())
                .detectedLanguage(entity.getDetectedLanguage())
                .detectionConfidence(entity.getDetectionConfidence())
                .processingTimeMs(entity.getProcessingTimeMs())
                .fromCache(entity.getFromCache())
                .cacheKey(entity.getCacheKey())
                .modelVersion(entity.getModelVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}
