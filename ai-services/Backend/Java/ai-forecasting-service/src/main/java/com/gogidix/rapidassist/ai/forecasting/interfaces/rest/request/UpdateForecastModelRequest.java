package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for updating a forecast model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateForecastModelRequest {

    private String modelName;

    private String description;

    private ModelStatus status;

    private String version;

    private Map<String, Object> hyperparameters;

    private Map<String, Object> trainingParameters;

    private Map<String, Object> metadata;
}
