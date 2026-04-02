package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for Prediction.
 * Maps to prediction collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "prediction")
public class PredictionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    private String modelName;

    // Input data stored as JSON
    private Map<String, Object> inputData;

    private Map<String, Object> preprocessedData;

    // Prediction result
    private Object predictionResult;

    private Double confidenceScore;

    private Map<String, Double> classProbabilities;

    // Status and execution
    @Indexed
    private PredictionStatus status;

    private String errorMessage;

    // Timestamps
    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    @Indexed
    private LocalDateTime completedAt;

    // Performance tracking
    private Long processingTimeMs;

    // Additional metadata stored as JSON
    private Map<String, Object> metadata;

    // Version for optimistic locking
    private Long version;
}
