package com.gogidix.rapidassist.ai.modelmanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.DeploymentStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a model deployment.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDeploymentDto {

    @EqualsAndHashCode.Include


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

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deployedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private String healthStatus;
    private Integer currentRequests;
    private Long totalRequests;
    private Double averageResponseTime;
    private Long version;
}
