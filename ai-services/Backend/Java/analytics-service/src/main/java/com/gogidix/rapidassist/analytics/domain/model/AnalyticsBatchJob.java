package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a batch analytics job.
 * Handles large-scale analytics computations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsBatchJob {

    private UUID id;
    private String tenantId;
    private String jobName;
    private String jobType;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer totalRecords;
    private Integer processedRecords;
    private Integer failedRecords;
    private Double progressPercentage;
    private Map<String, Object> jobParameters;
    private String dataSource;
    private String analyticsType;
    private Map<String, Object> result;
    private String error_message;
    private String triggeredBy;
    private String executionMode;
    private Integer retryCount;
    private Integer maxRetries;
    private String schedule;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Check if job is completed
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    /**
     * Business logic: Check if job is failed
     */
    public boolean isFailed() {
        return "FAILED".equals(status);
    }

    /**
     * Business logic: Check if job is in progress
     */
    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    /**
     * Business logic: Check if job can be retried
     */
    public boolean canRetry() {
        return isFailed() && (retryCount == null || retryCount < (maxRetries != null ? maxRetries : 3));
    }

    /**
     * Business logic: Update progress
     */
    public void updateProgress(int processed, int total) {
        this.processedRecords = processed;
        this.totalRecords = total;
        this.progressPercentage = total > 0 ? (processed * 100.0 / total) : 0.0;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as started
     */
    public void markAsStarted(String startedBy) {
        this.status = "IN_PROGRESS";
        this.startTime = LocalDateTime.now();
        this.triggeredBy = startedBy;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as completed
     */
    public void markAsCompleted(Map<String, Object> result) {
        this.status = "COMPLETED";
        this.endTime = LocalDateTime.now();
        this.result = result;
        this.progressPercentage = 100.0;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = "FAILED";
        this.endTime = LocalDateTime.now();
        this.error_message = errorMessage;
        this.retryCount = (this.retryCount == null ? 0 : this.retryCount) + 1;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get job duration in seconds
     */
    public Long getJobDurationInSeconds() {
        if (startTime == null || endTime == null) {
            return null;
        }
        return java.time.Duration.between(startTime, endTime).getSeconds();
    }

    /**
     * Business logic: Check if job is stale
     */
    public boolean isStale() {
        if (startTime == null) {
            return false;
        }
        return "IN_PROGRESS".equals(status) &&
               LocalDateTime.now().isAfter(startTime.plusHours(24));
    }
}
