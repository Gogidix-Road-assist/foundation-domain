package com.gogidix.rapidassist.ai.matching.interfaces.rest.request;

import com.gogidix.rapidassist.ai.matching.domain.model.BatchJobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST Request to update batch job status.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBatchJobStatusRequest {

    @NotNull(message = "Status is required")
    private BatchJobStatus status;

    private Integer processedCount;

    private Integer successCount;

    private Integer failureCount;

    private String errorMessage;
}
