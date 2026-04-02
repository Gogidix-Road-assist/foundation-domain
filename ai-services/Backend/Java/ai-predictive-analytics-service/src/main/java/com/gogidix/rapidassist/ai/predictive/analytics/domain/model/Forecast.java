package com.gogidix.rapidassist.ai.predictive.analytics.domain.model;

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
 * Domain model representing a time series forecast.
 * Contains forecasted values with confidence intervals and dates.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Forecast {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String forecastName;

    // Forecast configuration
    private LocalDateTime forecastStartDate;
    private LocalDateTime forecastEndDate;
    private Integer forecastHorizon;

    // Forecast results
    private List<ForecastDataPoint> forecastData;
    private List<ForecastDataPoint> upperBound;
    private List<ForecastDataPoint> lowerBound;

    // Historical data used
    private List<ForecastDataPoint> historicalData;
    private Integer historicalDataPoints;

    // Forecast metrics
    private Map<String, Double> accuracyMetrics;
    private Double meanAbsoluteError;
    private Double meanAbsolutePercentageError;

    // Status and metadata
    private ModelStatus status;
    private String frequency; // DAILY, WEEKLY, MONTHLY, etc.
    private Map<String, Object> metadata;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime generatedAt;

    /**
     * Business logic: Get total forecast points
     */
    public int getForecastPointCount() {
        return forecastData != null ? forecastData.size() : 0;
    }

    /**
     * Business logic: Check if forecast is complete
     */
    public boolean isComplete() {
        return ModelStatus.ACTIVE.equals(this.status) &&
               forecastData != null &&
               !forecastData.isEmpty();
    }

    /**
     * Business logic: Get forecast value for specific date
     */
    public Double getForecastValueForDate(LocalDateTime date) {
        if (forecastData == null) {
            return null;
        }
        return forecastData.stream()
                .filter(point -> point.getTimestamp().equals(date))
                .map(ForecastDataPoint::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Business logic: Initialize a new forecast
     */
    public static Forecast initialize(String tenantId, UUID modelId, String name,
                                     LocalDateTime startDate, LocalDateTime endDate) {
        return Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .forecastName(name)
                .forecastStartDate(startDate)
                .forecastEndDate(endDate)
                .status(ModelStatus.TRAINING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Nested class for forecast data points
     */
    @Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForecastDataPoint {
        private LocalDateTime timestamp;
        private Double value;
        private Double confidence;
        private Map<String, Object> metadata;
    }
}
