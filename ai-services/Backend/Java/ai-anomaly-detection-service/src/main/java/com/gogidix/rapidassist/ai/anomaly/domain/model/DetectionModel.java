package com.gogidix.rapidassist.ai.anomaly.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an ML model used for anomaly detection.
 * Contains model configuration, training information, and performance metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectionModel {

    private UUID id;
    private String tenantId;
    private String modelName;
    private String modelType;
    private String modelVersion;
    private String algorithm;
    private ModelStatus status;
    private String dataSource;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingConfig;
    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Integer truePositiveRate;
    private Integer falsePositiveRate;
    private LocalDateTime lastTrainedAt;
    private String trainedBy;
    private Integer trainingDataSize;
    private Integer validationDataSize;
    private Boolean isActive;
    private String modelPath;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;

    /**
     * Business logic: Activate model
     */
    public void activate() {
        if (this.status == ModelStatus.TRAINED) {
            this.isActive = true;
            this.updatedAt = LocalDateTime.now();
        } else {
            throw new IllegalStateException("Cannot activate model in status: " + this.status);
        }
    }

    /**
     * Business logic: Deactivate model
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Mark as trained
     */
    public void markAsTrained(String trainedBy, Integer trainingDataSize, Integer validationDataSize) {
        this.status = ModelStatus.TRAINED;
        this.lastTrainedAt = LocalDateTime.now();
        this.trainedBy = trainedBy;
        this.trainingDataSize = trainingDataSize;
        this.validationDataSize = validationDataSize;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Update performance metrics
     */
    public void updateMetrics(Double accuracy, Double precision, Double recall, Double f1Score) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1Score = f1Score;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if model is active
     */
    public boolean isActiveModel() {
        return this.isActive && ModelStatus.TRAINED.equals(this.status);
    }

    /**
     * Business logic: Check if model performance is acceptable
     */
    public boolean hasAcceptablePerformance(double minAccuracy, double minF1Score) {
        return this.accuracy != null && this.accuracy >= minAccuracy &&
               this.f1Score != null && this.f1Score >= minF1Score;
    }

    /**
     * Business logic: Check if needs retraining
     */
    public boolean needsRetraining(int retrainingThresholdDays) {
        if (lastTrainedAt == null) {
            return true;
        }
        long daysSinceTraining = java.time.Duration.between(lastTrainedAt, LocalDateTime.now()).toDays();
        return daysSinceTraining > retrainingThresholdDays;
    }
}
