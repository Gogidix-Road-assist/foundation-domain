package com.gogidix.rapidassist.ai.imagerecognition.application.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Query to get an image recognition by request ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetImageRecognitionByRequestIdQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "Request ID is required")
    private String requestId;
}
