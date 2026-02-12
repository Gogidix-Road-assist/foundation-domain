package com.gogidix.rapidassist.ai.inference.application.command;

import com.gogidix.rapidassist.ai.inference.domain.model.InferenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a new inference request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInferenceRequestCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Model ID is required")
    private String modelId;

    @NotBlank(message = "Model version is required")
    private String modelVersion;

    @NotNull(message = "Inference type is required")
    private InferenceType inferenceType;

    @NotBlank(message = "Input data is required")
    private String inputData;

    private Map<String, Object> parameters;

    private Integer priority;

    @NotBlank(message = "Requested by is required")
    private String requestedBy;
}
