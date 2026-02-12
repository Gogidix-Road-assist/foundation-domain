package com.gogidix.rapidassist.ai.forecasting.application.command;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to update a forecast model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateForecastModelCommand {

    private String tenantId;
    private UUID modelId;
    private String modelName;
    private String description;
    private ModelStatus status;
    private String version;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingParameters;
    private Map<String, Object> metadata;
    private String updatedBy;
}
