package com.gogidix.rapidassist.ai.computervision.application.command;

import com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to analyze an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyzeImageCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageStoragePath;

    @NotNull(message = "Image format is required")
    private ImageFormat format;

    private Long fileSize;

    private Integer width;

    private Integer height;

    @NotBlank(message = "Analysis type is required")
    private String analysisType;

    private String createdBy;
}
