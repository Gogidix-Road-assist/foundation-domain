package com.gogidix.rapidassist.ai.imagerecognition.application.command;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Command to create a new image recognition request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateImageRecognitionCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    private String imageStoragePath;

    @NotNull(message = "Recognition type is required")
    private RecognitionType recognitionType;

    private Map<String, Object> metadata;
}
