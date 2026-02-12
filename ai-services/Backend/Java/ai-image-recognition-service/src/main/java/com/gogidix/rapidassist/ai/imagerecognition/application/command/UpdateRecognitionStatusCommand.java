package com.gogidix.rapidassist.ai.imagerecognition.application.command;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to update recognition status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecognitionStatusCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Recognition ID is required")
    private UUID recognitionId;

    @NotNull(message = "Status is required")
    private RecognitionStatus status;

    private String errorMessage;
}
