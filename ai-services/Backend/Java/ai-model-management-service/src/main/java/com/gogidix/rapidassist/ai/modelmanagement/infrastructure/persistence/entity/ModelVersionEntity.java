package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

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
 * MongoDB Entity for ModelVersion.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "model_version")
public class ModelVersionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    @Indexed
    private String versionNumber;

    private String description;
    private String artifactPath;
    private Long artifactSize;
    private String checksum;
    private Map<String, Object> trainingMetrics;
    private Map<String, Object> validationMetrics;
    private Map<String, Object> hyperparameters;
    private String gitCommitHash;
    private String trainingDataSetId;
    private Boolean isProductionReady;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime deployedAt;
    private Long version;
}
