package com.gogidix.rapidassist.ai.inference.application.dto;

import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Model Version
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelVersionDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String modelId;
    private String tenantId;
    private String version;
    private ModelVersionStatus status;
    private String modelPath;
    private String modelFormat;
    private Map<String, Object> modelConfig;
    private Map<String, Object> performanceMetrics;
    private LocalDateTime createdAt;
    private LocalDateTime deployedAt;
    private Boolean isDefault;
    private String description;
    private String createdBy;
    private String tags;
}
