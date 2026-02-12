package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
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
 * MongoDB Document for PredictiveModel.
 * Maps to predictive_model collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "predictive_model")
public class PredictiveModelEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private ModelType modelType;

    @Indexed
    private ModelStatus status;

    private String algorithm;

    // Training configuration (stored as JSON)
    private Map<String, Object> trainingConfig;

    // Model performance metrics
    private Map<String, Double> performanceMetrics;

    // Model metadata
    private String modelVersion;
    private String featureCount;
    private String targetVariable;

    // Features stored as JSON array
    private String features;

    // Timestamps
    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime lastTrainedAt;

    private LocalDateTime deployedAt;

    // Audit fields
    private String createdBy;

    private String updatedBy;

    // Model storage location
    private String modelStoragePath;

    // Additional metadata stored as JSON
    private Map<String, Object> metadata;

    // Version for optimistic locking
    private Long version;
}
