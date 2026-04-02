package com.gogidix.rapidassist.ai.translation.domain.aggregate;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Translation Session.
 * Manages the lifecycle and business logic of a translation session.
 * Contains: TranslationRequest as child entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranslationSession {

    private UUID id;
    private String tenantId;
    private String userId;

    /**
     * Session identifier (external).
     */
    private String sessionId;

    /**
     * Session status.
     */
    @Builder.Default
    private SessionStatus status = SessionStatus.INITIALIZED;

    /**
     * Session type (REAL_TIME, BATCH, DOCUMENT).
     */
    private SessionType sessionType;

    /**
     * Default source language for the session.
     */
    private String defaultSourceLanguage;

    /**
     * Default target language for the session.
     */
    private String defaultTargetLanguage;

    /**
     * Channel/API endpoint used.
     */
    private String channel;

    /**
     * Additional metadata.
     */
    private java.util.Map<String, Object> metadata;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActivityAt;
    private String createdBy;
    private String updatedBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<TranslationRequest> translationRequests = new ArrayList<>();

    /**
     * Session status enumeration.
     */
    public enum SessionStatus {
        INITIALIZED,
        ACTIVE,
        PAUSED,
        COMPLETED,
        TERMINATED
    }

    /**
     * Session type enumeration.
     */
    public enum SessionType {
        REAL_TIME,
        BATCH,
        DOCUMENT
    }

    /**
     * Business logic: Initialize a new session.
     */
    public static TranslationSession initialize(String tenantId, String userId, SessionType sessionType,
                                                 String sourceLanguage, String targetLanguage, String channel) {
        return TranslationSession.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .sessionId(UUID.randomUUID().toString())
                .status(SessionStatus.INITIALIZED)
                .sessionType(sessionType)
                .defaultSourceLanguage(sourceLanguage)
                .defaultTargetLanguage(targetLanguage)
                .channel(channel)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .lastActivityAt(LocalDateTime.now())
                .translationRequests(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Activate session.
     */
    public void activate() {
        if (this.status == SessionStatus.INITIALIZED || this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.ACTIVE;
            this.updatedAt = LocalDateTime.now();
            this.lastActivityAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot activate session in status: " + this.status);
        }
    }

    /**
     * Business logic: Pause session.
     */
    public void pause() {
        if (this.status == SessionStatus.ACTIVE) {
            this.status = SessionStatus.PAUSED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot pause session in status: " + this.status);
        }
    }

    /**
     * Business logic: Complete session.
     */
    public void complete() {
        if (this.status == SessionStatus.ACTIVE || this.status == SessionStatus.PAUSED) {
            this.status = SessionStatus.COMPLETED;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot complete session in status: " + this.status);
        }
    }

    /**
     * Business logic: Terminate session.
     */
    public void terminate() {
        this.status = SessionStatus.TERMINATED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if session is active.
     */
    public boolean isActive() {
        return SessionStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Check if session can accept new requests.
     */
    public boolean canAcceptRequests() {
        return this.status == SessionStatus.ACTIVE || this.status == SessionStatus.INITIALIZED;
    }

    /**
     * Business logic: Add translation request to session.
     */
    public void addTranslationRequest(TranslationRequest request) {
        if (!canAcceptRequests()) {
            throw new IllegalStateException("Cannot add request to session in status: " + this.status);
        }
        request.setTranslationSessionId(this.id);
        request.setTenantId(this.tenantId);
        this.translationRequests.add(request);
        this.lastActivityAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get all completed requests.
     */
    public List<TranslationRequest> getCompletedRequests() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::isCompleted)
                .toList();
    }

    /**
     * Business logic: Get all failed requests.
     */
    public List<TranslationRequest> getFailedRequests() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::isFailed)
                .toList();
    }

    /**
     * Business logic: Get all in-progress requests.
     */
    public List<TranslationRequest> getInProgressRequests() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::isInProgress)
                .toList();
    }

    /**
     * Business logic: Get request count.
     */
    public int getRequestCount() {
        return this.translationRequests.size();
    }

    /**
     * Business logic: Get completed request count.
     */
    public long getCompletedRequestCount() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::isCompleted)
                .count();
    }

    /**
     * Business logic: Get failed request count.
     */
    public long getFailedRequestCount() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::isFailed)
                .count();
    }

    /**
     * Business logic: Calculate success rate.
     */
    public double getSuccessRate() {
        int total = getRequestCount();
        if (total == 0) {
            return 0.0;
        }
        long completed = getCompletedRequestCount();
        return (double) completed / total;
    }

    /**
     * Business logic: Calculate session duration in seconds.
     */
    public long getSessionDurationSeconds() {
        if (createdAt == null) {
            return 0;
        }
        LocalDateTime endTime = this.updatedAt != null ? this.updatedAt : LocalDateTime.now();
        return java.time.Duration.between(createdAt, endTime).getSeconds();
    }

    /**
     * Business logic: Get time since last activity in seconds.
     */
    public long getTimeSinceLastActivitySeconds() {
        if (lastActivityAt == null) {
            return 0;
        }
        return java.time.Duration.between(lastActivityAt, LocalDateTime.now()).getSeconds();
    }

    /**
     * Business logic: Check if session is idle (no activity for specified minutes).
     */
    public boolean isIdle(int idleThresholdMinutes) {
        long idleSeconds = getTimeSinceLastActivitySeconds();
        return idleSeconds > (idleThresholdMinutes * 60L);
    }

    /**
     * Business logic: Check if all requests are completed.
     */
    public boolean areAllRequestsCompleted() {
        return !this.translationRequests.isEmpty() &&
               this.translationRequests.stream().allMatch(TranslationRequest::isCompleted);
    }

    /**
     * Business logic: Check if session has any failed requests.
     */
    public boolean hasFailedRequests() {
        return this.translationRequests.stream().anyMatch(TranslationRequest::isFailed);
    }

    /**
     * Business logic: Calculate average processing time per request.
     */
    public double getAverageProcessingTimeMs() {
        return this.translationRequests.stream()
                .filter(r -> r.getProcessingTimeMs() != null)
                .mapToLong(TranslationRequest::getProcessingTimeMs)
                .average()
                .orElse(0.0);
    }

    /**
     * Business logic: Calculate average quality score.
     */
    public double getAverageQualityScore() {
        return this.translationRequests.stream()
                .filter(r -> r.getQuality() != null && r.getQuality().getScore() != null)
                .mapToDouble(r -> r.getQuality().getScore())
                .average()
                .orElse(0.0);
    }

    /**
     * Business logic: Count requests retrieved from cache.
     */
    public long getCachedRequestCount() {
        return this.translationRequests.stream()
                .filter(TranslationRequest::getFromCache)
                .count();
    }

    /**
     * Business logic: Calculate cache hit rate.
     */
    public double getCacheHitRate() {
        int total = getRequestCount();
        if (total == 0) {
            return 0.0;
        }
        long cached = getCachedRequestCount();
        return (double) cached / total;
    }

    /**
     * Business logic: Update session metadata.
     */
    public void updateMetadata(String key, Object value) {
        if (this.metadata == null) {
            this.metadata = new java.util.HashMap<>();
        }
        this.metadata.put(key, value);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get most used language pair.
     */
    public String getMostUsedLanguagePair() {
        return this.translationRequests.stream()
                .map(TranslationRequest::getLanguagePairCode)
                .collect(java.util.stream.Collectors.groupingBy(
                        java.util.function.Function.identity(),
                        java.util.stream.Collectors.counting()
                ))
                .entrySet().stream()
                .max(java.util.Map.Entry.comparingByValue())
                .map(java.util.Map.Entry::getKey)
                .orElse(null);
    }

    /**
     * Business logic: Get session statistics.
     */
    public java.util.Map<String, Object> getStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalRequests", getRequestCount());
        stats.put("completedRequests", getCompletedRequestCount());
        stats.put("failedRequests", getFailedRequestCount());
        stats.put("successRate", getSuccessRate());
        stats.put("cachedRequests", getCachedRequestCount());
        stats.put("cacheHitRate", getCacheHitRate());
        stats.put("averageProcessingTimeMs", getAverageProcessingTimeMs());
        stats.put("averageQualityScore", getAverageQualityScore());
        stats.put("durationSeconds", getSessionDurationSeconds());
        stats.put("mostUsedLanguagePair", getMostUsedLanguagePair());
        return stats;
    }
}
