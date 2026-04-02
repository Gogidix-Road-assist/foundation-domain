package com.gogidix.rapidassist.ai.summization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a summarization task.
 * Tracks the processing of document summarization requests.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizationTask {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String userId;
    private String taskId;
    private TaskStatus status;
    private DocumentType documentType;
    private SummaryStyle summaryStyle;
    private String sourceLanguage;
    private String targetLanguage;
    private Priority priority;
    private UUID summaryConfigId;
    private String inputText;
    private String inputUrl;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String createdBy;
    private String updatedBy;
    private Long processingTimeMs;
    private Integer retryCount;
    private Integer maxRetries;
    private java.util.Map<String, Object> metadata;

    // Associated entities
    @Builder.Default
    private List<DocumentSummary> summaries = new ArrayList<>();

    /**
     * Business logic: Initialize a new task
     */
    public static SummarizationTask initialize(String tenantId, String userId, DocumentType documentType, Priority priority) {
        return SummarizationTask.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .taskId(UUID.randomUUID().toString())
                .status(TaskStatus.PENDING)
                .documentType(documentType)
                .priority(priority != null ? priority : Priority.NORMAL)
                .retryCount(0)
                .maxRetries(3)
                .createdAt(LocalDateTime.now())
                .summaries(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start the task
     */
    public void start() {
        if (this.status != TaskStatus.PENDING) {
            throw new IllegalStateException("Cannot start task in status: " + this.status);
        }
        this.status = TaskStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete the task
     */
    public void complete() {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete task in status: " + this.status);
        }
        this.status = TaskStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.processingTimeMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }

    /**
     * Business logic: Fail the task
     */
    public void fail(String errorMessage) {
        if (this.status != TaskStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot fail task in status: " + this.status);
        }
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        if (this.startedAt != null) {
            this.processingTimeMs = java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
        }
    }

    /**
     * Business logic: Cancel the task
     */
    public void cancel() {
        if (this.status == TaskStatus.COMPLETED || this.status == TaskStatus.FAILED) {
            throw new IllegalStateException("Cannot cancel task in status: " + this.status);
        }
        this.status = TaskStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if can retry
     */
    public boolean canRetry() {
        return this.status == TaskStatus.FAILED
                && this.retryCount != null
                && this.maxRetries != null
                && this.retryCount < this.maxRetries;
    }

    /**
     * Business logic: Increment retry count
     */
    public void incrementRetry() {
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
        this.retryCount++;
        this.status = TaskStatus.PENDING;
        this.errorMessage = null;
    }

    /**
     * Business logic: Check if is pending
     */
    public boolean isPending() {
        return TaskStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Check if is in progress
     */
    public boolean isInProgress() {
        return TaskStatus.IN_PROGRESS.equals(this.status);
    }

    /**
     * Business logic: Check if is completed
     */
    public boolean isCompleted() {
        return TaskStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if is failed
     */
    public boolean isFailed() {
        return TaskStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if is cancelled
     */
    public boolean isCancelled() {
        return TaskStatus.CANCELLED.equals(this.status);
    }

    /**
     * Business logic: Check if has summaries
     */
    public boolean hasSummaries() {
        return summaries != null && !summaries.isEmpty();
    }

    /**
     * Business logic: Get summary count
     */
    public int getSummaryCount() {
        return summaries != null ? summaries.size() : 0;
    }

    /**
     * Business logic: Add summary
     */
    public void addSummary(DocumentSummary summary) {
        if (this.summaries == null) {
            this.summaries = new ArrayList<>();
        }
        summary.setSummarizationTaskId(this.id);
        summary.setTenantId(this.tenantId);
        this.summaries.add(summary);
    }

    /**
     * Business logic: Check if is high priority
     */
    public boolean isHighPriority() {
        return Priority.HIGH.equals(this.priority) || Priority.URGENT.equals(this.priority);
    }

    /**
     * Business logic: Check if is urgent
     */
    public boolean isUrgent() {
        return Priority.URGENT.equals(this.priority);
    }

    /**
     * Business logic: Check if has input
     */
    public boolean hasInput() {
        return (inputText != null && !inputText.trim().isEmpty())
                || (inputUrl != null && !inputUrl.trim().isEmpty());
    }

    /**
     * Business logic: Get task duration
     */
    public long getTaskDurationMs() {
        if (this.completedAt == null || this.startedAt == null) {
            return 0;
        }
        return java.time.Duration.between(this.startedAt, this.completedAt).toMillis();
    }

    /**
     * Business logic: Check if timed out
     */
    public boolean isTimedOut(long timeoutMs) {
        if (this.startedAt == null) {
            return false;
        }
        LocalDateTime now = this.completedAt != null ? this.completedAt : LocalDateTime.now();
        return java.time.Duration.between(this.startedAt, now).toMillis() > timeoutMs;
    }
}
