package com.gogidix.rapidassist.ai.forecasting.application.command;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a forecast model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateForecastModelCommand {

    private String tenantId;
    private String modelName;
    private String description;
    private ForecastModelType modelType;
    private String version;
    private Map<String, Object> hyperparameters;
    private Map<String, Object> trainingParameters;
    private Map<String, Object> metadata;
    private String createdBy;
}
