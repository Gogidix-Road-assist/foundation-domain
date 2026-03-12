package com.gogidix.rapidassist.ai.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an optimization job.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationJob {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private OptimizationType type;
    private OptimizationStatus status;
    private OptimizationAlgorithm algorithm;
    private OptimizationConfiguration configuration;
    private List<Hyperparameter> hyperparameters;
    private List<OptimizationResult> results;
    private String modelType;
    private String modelId;
    private String datasetId;
    private String objective;
    private String direction;
    private Integer currentIteration;
    private Integer maxIterations;
    private Double bestObjectiveValue;
    private Map<String, Object> bestParameters;
    private Double convergenceThreshold;
    private String convergenceStatus;
    private Long totalExecutionTimeMs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String error;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Check if job is in a terminal state.
     */
    public boolean isTerminal() {
        return status == OptimizationStatus.COMPLETED
                || status == OptimizationStatus.FAILED
                || status == OptimizationStatus.CANCELLED;
    }

    /**
     * Check if job is running.
     */
    public boolean isRunning() {
        return status == OptimizationStatus.RUNNING;
    }

    /**
     * Check if job can be started.
     */
    public boolean canStart() {
        return status == OptimizationStatus.PENDING || status == OptimizationStatus.PAUSED;
    }

    /**
     * Get job duration in seconds.
     */
    public Long getDurationSeconds() {
        if (startTime == null) {
            return 0L;
        }
        LocalDateTime end = endTime != null ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, end).getSeconds();
    }

    /**
     * Get progress percentage.
     */
    public Double getProgress() {
        if (maxIterations == null || maxIterations == 0) {
            return 0.0;
        }
        if (currentIteration == null) {
            return 0.0;
        }
        return (currentIteration.doubleValue() / maxIterations.doubleValue()) * 100.0;
    }

    /**
     * Mark job as started.
     */
    public void markAsStarted() {
        this.status = OptimizationStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.currentIteration = 0;
    }

    /**
     * Mark job as completed.
     */
    public void markAsCompleted() {
        this.status = OptimizationStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * Mark job as failed.
     */
    public void markAsFailed(String error) {
        this.status = OptimizationStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.error = error;
    }

    /**
     * Mark job as cancelled.
     */
    public void markAsCancelled() {
        this.status = OptimizationStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    /**
     * Pause the job.
     */
    public void pause() {
        if (this.status == OptimizationStatus.RUNNING) {
            this.status = OptimizationStatus.PAUSED;
        }
    }

    /**
     * Resume the job.
     */
    public void resume() {
        if (this.status == OptimizationStatus.PAUSED) {
            this.status = OptimizationStatus.RUNNING;
        }
    }

    /**
     * Increment iteration counter.
     */
    public void incrementIteration() {
        if (currentIteration == null) {
            currentIteration = 0;
        }
        currentIteration++;
        this.updatedAt = LocalDateTime.now();
    }
}
