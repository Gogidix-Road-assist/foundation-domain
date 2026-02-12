package com.gogidix.rapidassist.ai.dataquality.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain Model representing a data quality check execution.
 * Tracks individual validation runs against datasets.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityCheck {

    private UUID id;
    private String tenantId;
    private UUID ruleId;
    private String checkName;
    private String entityType;
    private String datasetIdentifier;
    private CheckStatus status;
    private int totalRecords;
    private int recordsChecked;
    private int passedRecords;
    private int failedRecords;
    private double passPercentage;
    private Map<String, Object> checkParameters;
    private LocalDateTime executedAt;
    private LocalDateTime completedAt;
    private long executionDurationMs;
    private String errorMessage;
    private Map<String, Object> metadata;
    private String executedBy;

    public enum CheckStatus {
        PENDING,
        RUNNING,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }

    /**
     * Business logic: Start the check
     */
    public void start() {
        this.status = CheckStatus.RUNNING;
        this.executedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete the check successfully
     */
    public void complete(int passed, int failed, int total) {
        this.status = CheckStatus.COMPLETED;
        this.passedRecords = passed;
        this.failedRecords = failed;
        this.totalRecords = total;
        this.recordsChecked = total;
        this.passPercentage = total > 0 ? (passed * 100.0 / total) : 0.0;
        this.completedAt = LocalDateTime.now();
        this.executionDurationMs = calculateDuration();
    }

    /**
     * Business logic: Mark check as failed
     */
    public void fail(String errorMessage) {
        this.status = CheckStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        this.executionDurationMs = calculateDuration();
    }

    /**
     * Business logic: Cancel the check
     */
    public void cancel() {
        this.status = CheckStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
        this.executionDurationMs = calculateDuration();
    }

    /**
     * Business logic: Mark check as timeout
     */
    public void timeout() {
        this.status = CheckStatus.TIMEOUT;
        this.completedAt = LocalDateTime.now();
        this.executionDurationMs = calculateDuration();
    }

    /**
     * Business logic: Update progress
     */
    public void updateProgress(int recordsChecked) {
        this.recordsChecked = recordsChecked;
    }

    /**
     * Business logic: Check if check passed
     */
    public boolean isPassed() {
        return this.status == CheckStatus.COMPLETED && this.failedRecords == 0;
    }

    /**
     * Business logic: Check if check failed
     */
    public boolean isFailed() {
        return this.status == CheckStatus.FAILED ||
               (this.status == CheckStatus.COMPLETED && this.failedRecords > 0);
    }

    /**
     * Business logic: Calculate execution duration
     */
    private long calculateDuration() {
        if (executedAt == null || completedAt == null) {
            return 0;
        }
        return java.time.Duration.between(executedAt, completedAt).toMillis();
    }

    /**
     * Business logic: Check if check is running
     */
    public boolean isRunning() {
        return this.status == CheckStatus.RUNNING;
    }

    /**
     * Business logic: Get pass rate as percentage
     */
    public double getPassRate() {
        return this.passPercentage;
    }
}
