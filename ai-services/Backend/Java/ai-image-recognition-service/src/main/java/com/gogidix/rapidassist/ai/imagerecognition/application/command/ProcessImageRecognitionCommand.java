package com.gogidix.rapidassist.ai.imagerecognition.application.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Command to process an image recognition request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessImageRecognitionCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Recognition ID is required")
    private UUID recognitionId;

    private String aiModelUsed;

    private Map<String, Object> results;
}
