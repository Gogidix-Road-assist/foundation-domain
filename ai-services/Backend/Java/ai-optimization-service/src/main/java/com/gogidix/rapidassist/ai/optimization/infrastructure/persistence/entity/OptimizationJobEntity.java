package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationStatus;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationType;
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
 * MongoDB Document for OptimizationJob.
 * Maps to optimization_job collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "optimization_job")
public class OptimizationJobEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private OptimizationType type;

    @Indexed
    private OptimizationStatus status;

    private OptimizationAlgorithm algorithm;

    private String configurationId;

    private String modelType;

    @Indexed
    private String modelId;

    @Indexed
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

    private String metadata;

    @Indexed
    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
