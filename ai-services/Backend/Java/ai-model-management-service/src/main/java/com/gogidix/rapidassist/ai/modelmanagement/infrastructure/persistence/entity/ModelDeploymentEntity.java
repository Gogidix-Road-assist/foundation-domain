package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.DeploymentStatus;
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
 * MongoDB Entity for ModelDeployment.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "model_deployment")
public class ModelDeploymentEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    @Indexed
    private UUID modelVersionId;

    @Indexed
    private String environment;

    private DeploymentStatus status;
    private String endpointUrl;
    private Integer instanceCount;
    private Integer cpuUnits;
    private Integer memoryMB;
    private String acceleratorType;
    private Integer acceleratorCount;
    private Map<String, String> environmentVariables;
    private String deploymentStrategy;
    private Map<String, Object> deploymentConfig;
    private String deployedBy;
    private LocalDateTime deployedAt;
    private LocalDateTime updatedAt;
    private String healthStatus;
    private Integer currentRequests;
    private Long totalRequests;
    private Double averageResponseTime;
    private Long version;
}
