package com.gogidix.rapidassist.ai.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing an optimization result.
 * Contains metrics and parameters from optimization iterations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizationResult {

    private UUID id;
    private String tenantId;
    private UUID optimizationJobId;
    private Integer iteration;
    private Double objectiveValue;
    private Double accuracy;
    private Double loss;
    private Double latency;
    private Double cost;
    private Map<String, Object> parameters;
    private List<String> metrics;
    private Map<String, Double> additionalMetrics;
    private String status;
    private Long executionTimeMs;
    private String convergenceStatus;
    private String notes;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private String createdBy;
}
