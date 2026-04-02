package com.gogidix.rapidassist.ai.predictive.analytics.domain.aggregate;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregate Root for Predictive Analytics.
 * Manages the lifecycle of predictive models and their associated predictions/forecasts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveAnalyticsAggregate {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;

    // Main predictive model
    private PredictiveModel model;

    // Associated predictions
    @Builder.Default
    private List<Prediction> predictions = new ArrayList<>();

    // Associated forecasts
    @Builder.Default
    private List<Forecast> forecasts = new ArrayList<>();

    // Metadata
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Business logic: Initialize a new predictive analytics aggregate
     */
    public static PredictiveAnalyticsAggregate initialize(String tenantId, String name, String description,
                                                          ModelType modelType, String algorithm) {
        PredictiveModel model = PredictiveModel.initialize(tenantId, name, modelType, algorithm);

        return PredictiveAnalyticsAggregate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .description(description)
                .model(model)
                .predictions(new ArrayList<>())
                .forecasts(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Business logic: Add a prediction to the aggregate
     */
    public void addPrediction(Prediction prediction) {
        prediction.setTenantId(this.tenantId);
        this.predictions.add(prediction);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add a forecast to the aggregate
     */
    public void addForecast(Forecast forecast) {
        forecast.setTenantId(this.tenantId);
        this.forecasts.add(forecast);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Get latest prediction
     */
    public Prediction getLatestPrediction() {
        return this.predictions.isEmpty() ? null :
               this.predictions.get(this.predictions.size() - 1);
    }

    /**
     * Business logic: Get latest forecast
     */
    public Forecast getLatestForecast() {
        return this.forecasts.isEmpty() ? null :
               this.forecasts.get(this.forecasts.size() - 1);
    }

    /**
     * Business logic: Get all successful predictions
     */
    public List<Prediction> getSuccessfulPredictions() {
        return this.predictions.stream()
                .filter(Prediction::isCompletedSuccessfully)
                .toList();
    }

    /**
     * Business logic: Get all failed predictions
     */
    public List<Prediction> getFailedPredictions() {
        return this.predictions.stream()
                .filter(Prediction::hasFailed)
                .toList();
    }

    /**
     * Business logic: Calculate prediction success rate
     */
    public double getPredictionSuccessRate() {
        if (this.predictions.isEmpty()) {
            return 0.0;
        }
        long successfulCount = this.predictions.stream()
                .filter(Prediction::isCompletedSuccessfully)
                .count();
        return (double) successfulCount / this.predictions.size() * 100.0;
    }

    /**
     * Business logic: Check if model can be used
     */
    public boolean isModelReady() {
        return this.model != null && this.model.canPredict();
    }

    /**
     * Business logic: Check if model needs retraining
     */
    public boolean needsModelRetraining(int thresholdDays) {
        return this.model != null && this.model.needsRetraining(thresholdDays);
    }

    /**
     * Business logic: Update model status
     */
    public void updateModelStatus(ModelStatus status) {
        if (this.model != null) {
            this.model.setStatus(status);
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Business logic: Get model accuracy
     */
    public Double getModelAccuracy() {
        return this.model != null ? this.model.getAccuracy() : null;
    }
}
