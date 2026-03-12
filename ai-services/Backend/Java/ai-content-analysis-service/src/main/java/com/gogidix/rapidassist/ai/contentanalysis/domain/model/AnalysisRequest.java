package com.gogidix.rapidassist.ai.contentanalysis.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing an analysis request.
 * Tracks request metadata for async processing.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String contentId;
    private String requestId;
    private RequestStatus status;
    private AnalysisType analysisType;
    private String contentType;

    private String callbackUrl;
    private Integer priority;
    private String requestedBy;
    private List<String> analysisOptions;

    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime estimatedCompletionAt;

    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private String lastAttemptAt;

    private UUID analysisResultId;
    private Integer progressPercentage;
    private String currentStep;

    /**
     * Business logic: Mark request as started
     */
    public void markAsStarted() {
        this.status = RequestStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        this.progressPercentage = 0;
        this.currentStep = "Initializing";
    }

    /**
     * Business logic: Mark request as completed
     */
    public void markAsCompleted(UUID analysisResultId) {
        this.status = RequestStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.analysisResultId = analysisResultId;
        this.progressPercentage = 100;
        this.currentStep = "Completed";
    }

    /**
     * Business logic: Mark request as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = RequestStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
        this.currentStep = "Failed";
    }

    /**
     * Business logic: Update progress
     */
    public void updateProgress(Integer percentage, String step) {
        this.progressPercentage = percentage;
        this.currentStep = step;
    }

    /**
     * Business logic: Increment retry count
     */
    public void incrementRetryCount() {
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
        this.lastAttemptAt = LocalDateTime.now().toString();
    }

    /**
     * Business logic: Check if can retry
     */
    public boolean canRetry() {
        return this.maxRetries == null || this.retryCount == null || this.retryCount < this.maxRetries;
    }

    /**
     * Business logic: Check if request is pending
     */
    public boolean isPending() {
        return RequestStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Check if request is in progress
     */
    public boolean isInProgress() {
        return RequestStatus.IN_PROGRESS.equals(this.status);
    }

    /**
     * Business logic: Check if request is completed
     */
    public boolean isCompleted() {
        return RequestStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if request is failed
     */
    public boolean isFailed() {
        return RequestStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Calculate processing duration in seconds
     */
    public Long getProcessingDurationSeconds() {
        if (this.startedAt == null || this.completedAt == null) {
            return null;
        }
        return java.time.Duration.between(this.startedAt, this.completedAt).getSeconds();
    }

    /**
     * Business logic: Check if request is high priority
     */
    public boolean isHighPriority() {
        return this.priority != null && this.priority >= 8;
    }

    /**
     * Enum for request status
     */
    public enum RequestStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    /**
     * Enum for analysis type
     */
    public enum AnalysisType {
        FULL,
        QUALITY_ONLY,
        READABILITY_ONLY,
        SENTIMENT_ONLY,
        SEO_ONLY,
        TOPICS_ONLY,
        CUSTOM
    }
}
