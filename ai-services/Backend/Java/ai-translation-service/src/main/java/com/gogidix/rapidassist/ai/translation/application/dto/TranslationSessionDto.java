package com.gogidix.rapidassist.ai.translation.application.dto;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO for TranslationSession.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationSessionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private String sessionId;
    private TranslationSession.SessionStatus status;
    private TranslationSession.SessionType sessionType;
    private String defaultSourceLanguage;
    private String defaultTargetLanguage;
    private String channel;
    private Object metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
    private String createdBy;
    private String updatedBy;

    private List<TranslationRequestDto> translationRequests;

    // Statistics
    private int requestCount;
    private long completedRequestCount;
    private long failedRequestCount;
    private double successRate;
    private long cachedRequestCount;
    private double cacheHitRate;
    private double averageProcessingTimeMs;
    private double averageQualityScore;
    private long durationSeconds;
    private String mostUsedLanguagePair;
}
