package com.gogidix.rapidassist.ai.forecasting.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a ForecastModel.
 * Pure domain model without MongoDB annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastModel {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String modelName;
    private String description;
    private ForecastModelType modelType;
    private ModelStatus status;
    private String version;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingParameters;
    private Integer trainingDataPoints;
    private BigDecimal trainingAccuracy;
    private BigDecimal validationAccuracy;
    private BigDecimal testAccuracy;
    private String featureImportance;
    private Map<String, Object> metadata;
    private LocalDateTime lastTrainedAt;
    private LocalDateTime deployedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Business logic: Check if model is deployed
     */
    public boolean isDeployed() {
        return ModelStatus.DEPLOYED.equals(this.status);
    }

    /**
     * Business logic: Check if model is trained
     */
    public boolean isTrained() {
        return ModelStatus.TRAINED.equals(this.status) || ModelStatus.DEPLOYED.equals(this.status);
    }

    /**
     * Business logic: Check if model is training
     */
    public boolean isTraining() {
        return ModelStatus.TRAINING.equals(this.status);
    }

    /**
     * Business logic: Get overall accuracy score
     */
    public BigDecimal getOverallAccuracy() {
        if (testAccuracy != null) {
            return testAccuracy;
        } else if (validationAccuracy != null) {
            return validationAccuracy;
        } else if (trainingAccuracy != null) {
            return trainingAccuracy;
        }
        return BigDecimal.ZERO;
    }

    /**
     * Business logic: Validate model has hyperparameters
     */
    public boolean hasHyperparameters() {
        return hyperparameters != null && !hyperparameters.isEmpty();
    }

    /**
     * Business logic: Get training duration in days
     */
    public long getTrainingDurationDays() {
        if (lastTrainedAt != null && createdAt != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(createdAt, lastTrainedAt);
        }
        return 0;
    }
}
