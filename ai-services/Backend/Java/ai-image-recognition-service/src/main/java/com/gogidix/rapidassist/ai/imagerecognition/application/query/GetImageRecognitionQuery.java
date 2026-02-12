package com.gogidix.rapidassist.ai.imagerecognition.application.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get an image recognition by ID.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetImageRecognitionQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Recognition ID is required")
    private UUID recognitionId;
}
