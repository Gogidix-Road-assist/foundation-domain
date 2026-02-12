package com.gogidix.rapidassist.ai.modelmanagement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a training job for model training.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainingJob {

    private UUID id;
    private String tenantId;
    private UUID modelId;
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
