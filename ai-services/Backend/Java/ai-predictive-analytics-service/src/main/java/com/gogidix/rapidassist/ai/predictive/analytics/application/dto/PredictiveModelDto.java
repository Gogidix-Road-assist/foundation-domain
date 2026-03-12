package com.gogidix.rapidassist.ai.predictive.analytics.application.dto;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for PredictiveModel
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictiveModelDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ModelType modelType;
    private ModelStatus status;
    private String algorithm;
    private Map<String, Double> performanceMetrics;
    private String modelVersion;
    private List<String> features;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastTrainedAt;
    private LocalDateTime deployedAt;
}
