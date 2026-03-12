package com.gogidix.rapidassist.ai.inference.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing a model version
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "model_versions")
public class ModelVersion {

    @Id
    @EqualsAndHashCode.Include

    private UUID id;

    @Indexed
    private String modelId;

    @Indexed
    private String tenantId;

    @Indexed
    private String version;

    private ModelVersionStatus status;
    private String modelPath;
    private String modelFormat;
    private java.util.Map<String, Object> modelConfig;
    private java.util.Map<String, Object> performanceMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime deployedAt;
    private Boolean isDefault;
    private String description;
    private String createdBy;
    private String tags;

    public static ModelVersion create(String tenantId, String modelId, String version,
                                     String modelPath, String modelFormat, String createdBy) {
        return ModelVersion.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .version(version)
                .status(ModelVersionStatus.STAGING)
                .modelPath(modelPath)
                .modelFormat(modelFormat)
                .modelConfig(new java.util.HashMap<>())
                .performanceMetrics(new java.util.HashMap<>())
                .createdAt(LocalDateTime.now())
                .isDefault(false)
                .createdBy(createdBy)
                .build();
    }

    public void activate() {
        this.status = ModelVersionStatus.ACTIVE;
        this.deployedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = ModelVersionStatus.INACTIVE;
    }

    public void retire() {
        this.status = ModelVersionStatus.RETIRED;
    }

    public boolean isActive() {
        return ModelVersionStatus.ACTIVE.equals(this.status);
    }

    public void addModelConfig(String key, Object value) {
        if (this.modelConfig == null) {
            this.modelConfig = new java.util.HashMap<>();
        }
        this.modelConfig.put(key, value);
    }

    public void addPerformanceMetric(String key, Object value) {
        if (this.performanceMetrics == null) {
            this.performanceMetrics = new java.util.HashMap<>();
        }
        this.performanceMetrics.put(key, value);
    }
}
