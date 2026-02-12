package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationStatus;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for TranslationRequest.
 * Maps to translation_request collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "translation_request")
public class TranslationRequestEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID translationSessionId;

    @Indexed
    private String sourceLanguage;

    @Indexed
    private String targetLanguage;

    @Indexed
    private TranslationStatus status;

    private String sourceText;

    private String translatedText;

    private String detectedLanguage;

    private Double detectionConfidence;

    private Long processingTimeMs;

    @Indexed
    private Boolean fromCache;

    private String cacheKey;

    private String modelVersion;

    private String metadata;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime completedAt;

    private Long version;
}
