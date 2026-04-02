package com.gogidix.rapidassist.ai.translation.application.mapper;

import com.gogidix.rapidassist.ai.translation.application.dto.TranslationQualityDto;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationRequestDto;
import com.gogidix.rapidassist.ai.translation.application.dto.TranslationSessionDto;
import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationQuality;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between TranslationSession domain and DTO.
 */
@Component
public class TranslationSessionMapper {

    public TranslationSessionDto toDto(TranslationSession domain) {
        if (domain == null) {
            return null;
        }

        List<TranslationRequestDto> requestDtos = domain.getTranslationRequests().stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());

        return TranslationSessionDto.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .userId(domain.getUserId())
                .sessionId(domain.getSessionId())
                .status(domain.getStatus())
                .sessionType(domain.getSessionType())
                .defaultSourceLanguage(domain.getDefaultSourceLanguage())
                .defaultTargetLanguage(domain.getDefaultTargetLanguage())
                .channel(domain.getChannel())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .lastActivityAt(domain.getLastActivityAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .translationRequests(requestDtos)
                .requestCount(domain.getRequestCount())
                .completedRequestCount(domain.getCompletedRequestCount())
                .failedRequestCount(domain.getFailedRequestCount())
                .successRate(domain.getSuccessRate())
                .cachedRequestCount(domain.getCachedRequestCount())
                .cacheHitRate(domain.getCacheHitRate())
                .averageProcessingTimeMs(domain.getAverageProcessingTimeMs())
                .averageQualityScore(domain.getAverageQualityScore())
                .durationSeconds(domain.getSessionDurationSeconds())
                .mostUsedLanguagePair(domain.getMostUsedLanguagePair())
                .build();
    }

    public TranslationRequestDto toRequestDto(TranslationRequest domain) {
        if (domain == null) {
            return null;
        }

        TranslationQualityDto qualityDto = domain.getQuality() != null ?
                toQualityDto(domain.getQuality()) : null;

        return TranslationRequestDto.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .translationSessionId(domain.getTranslationSessionId())
                .sourceText(domain.getSourceText())
                .sourceLanguage(domain.getSourceLanguage())
                .targetLanguage(domain.getTargetLanguage())
                .translatedText(domain.getTranslatedText())
                .status(domain.getStatus())
                .quality(qualityDto)
                .detectedLanguage(domain.getDetectedLanguage())
                .detectionConfidence(domain.getDetectionConfidence())
                .processingTimeMs(domain.getProcessingTimeMs())
                .fromCache(domain.getFromCache())
                .cacheKey(domain.getCacheKey())
                .modelVersion(domain.getModelVersion())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .completedAt(domain.getCompletedAt())
                .build();
    }

    public TranslationQualityDto toQualityDto(TranslationQuality domain) {
        if (domain == null) {
            return null;
        }

        return TranslationQualityDto.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .translationRequestId(domain.getTranslationRequestId())
                .score(domain.getScore())
                .confidence(domain.getConfidence() != null ? domain.getConfidence().name() : null)
                .errorCount(domain.getErrorCount())
                .warningCount(domain.getWarningCount())
                .fluencyScore(domain.getFluencyScore())
                .accuracyScore(domain.getAccuracyScore())
                .consistencyScore(domain.getConsistencyScore())
                .metrics(domain.getMetrics())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
