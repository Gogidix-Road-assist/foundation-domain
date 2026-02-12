package com.gogidix.rapidassist.ai.computervision.application.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Command to detect faces in an image
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetectFacesCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageStoragePath;

    private Integer width;

    private Integer height;

    private Boolean detectEmotions;

    private Boolean detectAge;

    private Boolean detectGender;

    private Boolean detectLandmarks;

    private Double minConfidence;

    private String createdBy;
}
