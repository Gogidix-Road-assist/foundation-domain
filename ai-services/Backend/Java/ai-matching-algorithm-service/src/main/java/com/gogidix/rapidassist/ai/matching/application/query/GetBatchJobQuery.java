package com.gogidix.rapidassist.ai.matching.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

/**
 * Query to get a batch matching job.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBatchJobQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    private java.util.UUID jobId;

    private String jobCode;
}
