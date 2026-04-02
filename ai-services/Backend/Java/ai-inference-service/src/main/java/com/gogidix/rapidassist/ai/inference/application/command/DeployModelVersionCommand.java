package com.gogidix.rapidassist.ai.inference.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to deploy a model version
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeployModelVersionCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Model ID is required")
    private String modelId;

    @NotBlank(message = "Version is required")
    private String version;

    @NotBlank(message = "Deployed by is required")
    private String deployedBy;
}
