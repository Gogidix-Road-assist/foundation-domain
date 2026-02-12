package com.gogidix.rapidassist.ai.riskassessment.application.command;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Command to update risk assessment status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssessmentStatusCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Risk assessment ID is required")
    private UUID riskAssessmentId;

    @NotNull(message = "Status is required")
    private AssessmentStatus status;

    private String reason;
    private String updatedBy;
}
