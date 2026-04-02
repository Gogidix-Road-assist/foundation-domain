package com.gogidix.rapidassist.ai.computervision.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to classify an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassifyImageCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageStoragePath;

    @NotNull(message = "Width is required")
    private Integer width;

    @NotNull(message = "Height is required")
    private Integer height;

    private Integer topPredictions;

    private Double minConfidence;

    private String modelName;

    private String createdBy;
}
