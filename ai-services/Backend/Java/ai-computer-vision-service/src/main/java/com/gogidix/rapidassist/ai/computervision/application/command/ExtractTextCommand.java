package com.gogidix.rapidassist.ai.computervision.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to extract text from an image (OCR)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractTextCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageStoragePath;

    private String language;

    private Boolean preserveLayout;

    private Boolean extractWords;

    private Double minConfidence;

    private String createdBy;
}
