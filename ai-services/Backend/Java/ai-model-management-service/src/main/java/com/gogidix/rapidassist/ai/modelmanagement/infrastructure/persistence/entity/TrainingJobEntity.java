package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.TrainingJobStatus;
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
 * MongoDB Entity for TrainingJob.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "training_job")
public class TrainingJobEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID modelId;

    @Indexed
    private String name;

    private String description;
    private TrainingJobStatus status;
    private String trainingDataSetId;
    private String validationDataSetId;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingConfig;
    private String algorithmType;
    private Integer epochs;
    private Integer batchSize;
    private Double learningRate;
    private String executorType;
    private Integer cpuUnits;
    private Integer memoryMB;
    private String acceleratorType;
    private Integer acceleratorCount;
    private String outputArtifactsPath;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long durationSeconds;
    private Double progressPercentage;
    private String currentEpoch;
    private Map<String, Double> trainingMetrics;
    private Map<String, Double> validationMetrics;
    private String errorMessage;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
