package com.gogidix.rapidassist.ai.anomaly.application.dto;

import com.gogidix.rapidassist.ai.anomaly.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DetectionModel
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DetectionModelDto {

    @EqualsAndHashCode.Include
    private UUID id;
    private String tenantId;
    private String modelName;
    private String modelType;
    private String modelVersion;
    private String algorithm;
    private ModelStatus status;
    private String dataSource;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingConfig;
    private Double accuracy;
    private Double precision;
    private Double recall;
    private Double f1Score;
    private Integer truePositiveRate;
    private Integer falsePositiveRate;
    private LocalDateTime lastTrainedAt;
    private String trainedBy;
    private Integer trainingDataSize;
    private Integer validationDataSize;
    private Boolean isActive;
    private String modelPath;
    private Map<String, Object> metadata;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long version;
}
