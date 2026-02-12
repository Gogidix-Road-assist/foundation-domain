package com.gogidix.rapidassist.ai.optimization.application.dto;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for OptimizationConfiguration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationConfigurationDto {

    private UUID id;
    private String tenantId;
    private OptimizationAlgorithm algorithm;
    private Integer maxIterations;
    private Integer populationSize;
    private Double mutationRate;
    private Double crossoverRate;
    private Double learningRate;
    private Double tolerance;
    private Long timeoutSeconds;
    private Integer parallelJobs;
    private String objective;
    private String direction;
    private Map<String, Object> algorithmParameters;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
