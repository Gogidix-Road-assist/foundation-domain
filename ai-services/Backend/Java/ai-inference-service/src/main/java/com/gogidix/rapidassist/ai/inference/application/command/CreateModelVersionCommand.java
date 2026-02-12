package com.gogidix.rapidassist.ai.inference.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a new model version
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateModelVersionCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Model ID is required")
    private String modelId;

    @NotBlank(message = "Version is required")
    private String version;

    @NotBlank(message = "Model path is required")
    private String modelPath;

    @NotBlank(message = "Model format is required")
    private String modelFormat;

    private Map<String, Object> modelConfig;

    private String description;

    private String tags;

    private Boolean isDefault;

    @NotBlank(message = "Created by is required")
    private String createdBy;
}
