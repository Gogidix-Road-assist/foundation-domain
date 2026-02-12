package com.gogidix.rapidassist.ai.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing optimization configuration.
 * Contains algorithm-specific settings and parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationConfiguration {

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
