package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Request DTO for creating a forecast model.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateForecastModelRequest {

    @NotBlank
    private String modelName;

    private String description;

    @NotNull
    private ForecastModelType modelType;

    private String version;

    private Map<String, Object> hyperparameters;

    private Map<String, Object> trainingParameters;

    private Map<String, Object> metadata;
}
