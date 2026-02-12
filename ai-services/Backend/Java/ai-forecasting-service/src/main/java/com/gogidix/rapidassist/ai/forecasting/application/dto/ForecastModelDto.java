package com.gogidix.rapidassist.ai.forecasting.application.dto;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModel;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ForecastModel.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForecastModelDto {

    private UUID id;
    private String tenantId;
    private String modelName;
    private String description;
    private ForecastModelType modelType;
    private ModelStatus status;
    private String version;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingParameters;
    private Integer trainingDataPoints;
    private BigDecimal trainingAccuracy;
    private BigDecimal validationAccuracy;
    private BigDecimal testAccuracy;
    private String featureImportance;
    private Map<String, Object> metadata;
    private LocalDateTime lastTrainedAt;
    private LocalDateTime deployedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
