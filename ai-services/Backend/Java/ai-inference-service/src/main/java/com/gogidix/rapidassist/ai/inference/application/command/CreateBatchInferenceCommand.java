package com.gogidix.rapidassist.ai.inference.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Command to create a batch inference request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBatchInferenceCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Model ID is required")
    private String modelId;

    @NotBlank(message = "Model version is required")
    private String modelVersion;

    @NotEmpty(message = "Input items cannot be empty")
    private List<String> inputItems;

    private Map<String, Object> parameters;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
