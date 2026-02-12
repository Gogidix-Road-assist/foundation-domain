package com.gogidix.rapidassist.ai.optimization.application.dto;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationConfiguration;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationResult;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationStatus;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for OptimizationJob.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationJobDto {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private OptimizationType type;
    private OptimizationStatus status;
    private OptimizationAlgorithm algorithm;
    private OptimizationConfigurationDto configuration;
    private List<HyperparameterDto> hyperparameters;
    private List<OptimizationResultDto> results;
    private String modelType;
    private String modelId;
    private String datasetId;
    private String objective;
    private String direction;
    private Integer currentIteration;
    private Integer maxIterations;
    private Double bestObjectiveValue;
    private Map<String, Object> bestParameters;
    private Double convergenceThreshold;
    private String convergenceStatus;
    private Long totalExecutionTimeMs;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String error;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double progress;
    private Long durationSeconds;
    private Boolean isTerminal;
    private Boolean isRunning;
}
