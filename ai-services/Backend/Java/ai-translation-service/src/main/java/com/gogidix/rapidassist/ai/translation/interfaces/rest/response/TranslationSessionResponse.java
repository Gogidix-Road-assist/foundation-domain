package com.gogidix.rapidassist.ai.translation.interfaces.rest.response;

import com.gogidix.rapidassist.ai.translation.domain.aggregate.TranslationSession;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response for translation session operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationSessionResponse {

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

    // Statistics
    private Integer requestCount;
    private Long completedRequestCount;
    private Long failedRequestCount;
    private Double successRate;
    private Long cachedRequestCount;
    private Double cacheHitRate;
    private Double averageProcessingTimeMs;
    private Double averageQualityScore;
    private Long durationSeconds;
    private String mostUsedLanguagePair;
}
