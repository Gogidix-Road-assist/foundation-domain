package com.gogidix.rapidassist.ai.modelmanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a version of an ML model.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelVersionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID modelId;
    private String versionNumber;
    private String description;
    private String artifactPath;
    private Long artifactSize;
    private String checksum;
    private Map<String, Object> trainingMetrics;
    private Map<String, Object> validationMetrics;
    private Map<String, Object> hyperparameters;
    private String gitCommitHash;
    private String trainingDataSetId;
    private Boolean isProductionReady;
    private String createdBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deployedAt;

    private Long version;
}
