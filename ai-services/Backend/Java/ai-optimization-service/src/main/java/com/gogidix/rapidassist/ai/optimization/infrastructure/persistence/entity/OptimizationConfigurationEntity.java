package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for OptimizationConfiguration.
 * Maps to optimization_configuration collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "optimization_configuration")
public class OptimizationConfigurationEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
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

    private String algorithmParameters;

    private String description;

    @Indexed
    private String createdBy;

    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long version;
}
