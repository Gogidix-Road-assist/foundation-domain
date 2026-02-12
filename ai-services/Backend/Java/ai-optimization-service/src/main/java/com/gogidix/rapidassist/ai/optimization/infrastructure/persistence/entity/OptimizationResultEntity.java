package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity;

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
 * MongoDB Document for OptimizationResult.
 * Maps to optimization_result collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "optimization_result")
public class OptimizationResultEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID optimizationJobId;

    private Integer iteration;

    @Indexed
    private Double objectiveValue;

    private Double accuracy;

    private Double loss;

    private Double latency;

    private Double cost;

    private String parameters;

    private String metrics;

    private String additionalMetrics;

    private String status;

    private Long executionTimeMs;

    private String convergenceStatus;

    private String notes;

    private String metadata;

    @Indexed
    private String createdBy;

    private LocalDateTime createdAt;
}
