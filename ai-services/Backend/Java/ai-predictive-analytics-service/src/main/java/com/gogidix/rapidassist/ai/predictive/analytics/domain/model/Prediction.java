package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a prediction request and its result.
 * Tracks the execution and outcome of predictive analytics operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prediction {

    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String modelName;

    // Input data
    private Map<String, Object> inputData;
    private Map<String, Object> preprocessedData;

    // Prediction result
    private Object predictionResult;
    private Double confidenceScore;
    private Map<String, Double> classProbabilities;

    // Status and execution
    private PredictionStatus status;
    private String errorMessage;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private LocalDateTime completedAt;

    // Performance tracking
    private Long processingTimeMs;

    // Additional metadata
    private Map<String, Object> metadata;

    /**
     * Business logic: Check if prediction is completed successfully
     */
    public boolean isCompletedSuccessfully() {
        return PredictionStatus.COMPLETED.equals(this.status);
    }

    /**
     * Business logic: Check if prediction failed
     */
    public boolean hasFailed() {
        return PredictionStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Check if prediction is still processing
     */
    public boolean isProcessing() {
        return PredictionStatus.PROCESSING.equals(this.status) ||
               PredictionStatus.PENDING.equals(this.status);
    }

    /**
     * Business logic: Initialize a new prediction request
     */
    public static Prediction initialize(String tenantId, UUID modelId, String modelName, Map<String, Object> inputData) {
        return Prediction.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .modelName(modelName)
                .inputData(inputData)
                .status(PredictionStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Mark prediction as processing
     */
    public void markAsProcessing() {
        this.status = PredictionStatus.PROCESSING;
        this.processedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark prediction as completed with result
     */
    public void markAsCompleted(Object result, Double confidence) {
        this.status = PredictionStatus.COMPLETED;
        this.predictionResult = result;
        this.confidenceScore = confidence;
        this.completedAt = LocalDateTime.now();

        if (this.processedAt != null) {
            this.processingTimeMs = java.time.Duration.between(this.processedAt, this.completedAt).toMillis();
        }
    }

    /**
     * Business logic: Mark prediction as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = PredictionStatus.FAILED;
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();

        if (this.processedAt != null) {
            this.processingTimeMs = java.time.Duration.between(this.processedAt, this.completedAt).toMillis();
        }
    }

    /**
     * Business logic: Calculate processing time if not set
     */
    public long calculateProcessingTime() {
        if (this.processingTimeMs != null) {
            return this.processingTimeMs;
        }
        if (this.processedAt != null && this.completedAt != null) {
            this.processingTimeMs = java.time.Duration.between(this.processedAt, this.completedAt).toMillis();
        }
        return this.processingTimeMs != null ? this.processingTimeMs : 0L;
    }
}
