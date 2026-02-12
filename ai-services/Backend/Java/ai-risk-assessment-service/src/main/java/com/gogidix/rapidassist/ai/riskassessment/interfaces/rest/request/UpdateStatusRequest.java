package com.gogidix.rapidassist.ai.riskassessment.interfaces.rest.request;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AssessmentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST Request to update assessment status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private AssessmentStatus status;

    private String reason;
}
