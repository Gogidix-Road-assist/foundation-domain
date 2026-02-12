package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelType;
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
 * MongoDB Entity for Model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "model")
public class ModelEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
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
