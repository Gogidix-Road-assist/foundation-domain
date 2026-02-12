package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a predictive model in the system.
 * Contains model configuration, training parameters, and performance metrics.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveModel {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ModelType modelType;
    private ModelStatus status;
    private String algorithm;

    // Training configuration
    private ModelTrainingConfig trainingConfig;

    // Model performance metrics
    private Map<String, Double> performanceMetrics;

    // Model metadata
    private String modelVersion;
    private String featureCount;
    private String targetVariable;
    private List<String> features;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTrainedAt;
    private LocalDateTime deployedAt;

    // Audit fields
    private String createdBy;
    private String updatedBy;

    // Model storage location
    private String modelStoragePath;

    // Additional metadata
    private Map<String, Object> metadata;

    /**
     * Business logic: Check if model is active and ready for predictions
     */
    public boolean isActive() {
        return ModelStatus.ACTIVE.equals(this.status);
    }

    /**
     * Business logic: Check if model is currently training
     */
    public boolean isTraining() {
        return ModelStatus.TRAINING.equals(this.status) ||
               ModelStatus.RETRAINING.equals(this.status);
    }

    /**
     * Business logic: Check if model can be used for predictions
     */
    public boolean canPredict() {
        return ModelStatus.ACTIVE.equals(this.status) ||
               ModelStatus.PAUSED.equals(this.status);
    }

    /**
     * Business logic: Check if model can be retrained
     */
    public boolean canRetrain() {
        return ModelStatus.ACTIVE.equals(this.status) ||
               ModelStatus.PAUSED.equals(this.status) ||
               ModelStatus.TRAINED.equals(this.status) ||
               ModelStatus.FAILED.equals(this.status);
    }

    /**
     * Business logic: Get accuracy score
     */
    public Double getAccuracy() {
        return performanceMetrics != null ? performanceMetrics.get("accuracy") : null;
    }

    /**
     * Business logic: Get precision score
     */
    public Double getPrecision() {
        return performanceMetrics != null ? performanceMetrics.get("precision") : null;
    }

    /**
     * Business logic: Get recall score
     */
    public Double getRecall() {
        return performanceMetrics != null ? performanceMetrics.get("recall") : null;
    }

    /**
     * Business logic: Get F1 score
     */
    public Double getF1Score() {
        return performanceMetrics != null ? performanceMetrics.get("f1Score") : null;
    }

    /**
     * Business logic: Get mean squared error (for regression models)
     */
    public Double getMeanSquaredError() {
        return performanceMetrics != null ? performanceMetrics.get("mse") : null;
    }

    /**
     * Business logic: Get R-squared score (for regression models)
     */
    public Double getRSquared() {
        return performanceMetrics != null ? performanceMetrics.get("r2") : null;
    }

    /**
     * Business logic: Check if model needs retraining based on last trained date
     */
    public boolean needsRetraining(int retrainingThresholdDays) {
        if (lastTrainedAt == null) {
            return true;
        }
        LocalDateTime threshold = LocalDateTime.now().minusDays(retrainingThresholdDays);
        return lastTrainedAt.isBefore(threshold);
    }

    /**
     * Business logic: Initialize a new model
     */
    public static PredictiveModel initialize(String tenantId, String name, ModelType modelType, String algorithm) {
        return PredictiveModel.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .modelType(modelType)
                .algorithm(algorithm)
                .status(ModelStatus.DRAFT)
                .modelVersion("1.0.0")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
