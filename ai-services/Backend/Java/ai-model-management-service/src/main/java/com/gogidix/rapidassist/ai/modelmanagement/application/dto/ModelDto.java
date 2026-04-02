package com.gogidix.rapidassist.ai.modelmanagement.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a registered ML model.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ModelType modelType;
    private ModelStatus status;
    private String framework;
    private String modelVersion;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> metadata;
    private String artifactPath;
    private String configurationPath;
    private String createdBy;
    private String updatedBy;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastDeployedAt;

    private Long version;
}
