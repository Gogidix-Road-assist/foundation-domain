package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a registered ML model.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Model {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ModelType modelType;
    private ModelStatus status;
    private String framework;
    private String modelVersion;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> metadata;
    private String artifactPath;
    private String configurationPath;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastDeployedAt;
    private Long version;
}
