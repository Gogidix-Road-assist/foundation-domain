package com.gogidix.rapidassist.ai.translation.application.dto;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationStatus;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationQuality;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for TranslationRequest.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationRequestDto {

    private UUID id;
    private String tenantId;
    private UUID translationSessionId;
    private String sourceText;
    private String sourceLanguage;
    private String targetLanguage;
    private String translatedText;
    private TranslationStatus status;
    private TranslationQualityDto quality;
    private String detectedLanguage;
    private Double detectionConfidence;
    private Long processingTimeMs;
    private Boolean fromCache;
    private String cacheKey;
    private String modelVersion;
    private Object metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
