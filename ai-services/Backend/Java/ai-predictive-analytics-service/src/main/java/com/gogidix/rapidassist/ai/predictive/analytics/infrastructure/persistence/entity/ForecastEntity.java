package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for Forecast.
 * Maps to forecast collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "forecast")
public class ForecastEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    private String forecastName;

    // Forecast configuration
    @Indexed
    private LocalDateTime forecastStartDate;

    @Indexed
    private LocalDateTime forecastEndDate;

    private Integer forecastHorizon;

    // Forecast results stored as JSON
    private List<Map<String, Object>> forecastData;

    private List<Map<String, Object>> upperBound;

    private List<Map<String, Object>> lowerBound;

    // Historical data
    private List<Map<String, Object>> historicalData;

    private Integer historicalDataPoints;

    // Forecast metrics
    private Map<String, Double> accuracyMetrics;

    private Double meanAbsoluteError;

    private Double meanAbsolutePercentageError;

    // Status and metadata
    @Indexed
    private ModelStatus status;

    private String frequency;

    private Map<String, Object> metadata;

    // Timestamps
    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime generatedAt;

    // Version for optimistic locking
    private Long version;
}
