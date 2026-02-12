package com.gogidix.rapidassist.ai.matching.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Command to update batch job status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBatchJobStatusCommand {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Job ID is required")
    private java.util.UUID jobId;

    @NotNull(message = "Status is required")
    private com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus status;

    private Integer processedCount;

    private Integer successCount;

    private Integer failureCount;

    private String errorMessage;
}
