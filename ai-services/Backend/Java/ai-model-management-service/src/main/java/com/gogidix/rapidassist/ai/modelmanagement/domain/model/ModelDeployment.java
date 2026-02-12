package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a model deployment.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDeployment {

    private UUID id;
    private String tenantId;
    private UUID modelId;
    private UUID modelVersionId;
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
