package com.gogidix.rapidassist.ai.inference.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get a batch inference request
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBatchInferenceRequestQuery {

    @NotNull(message = "Batch request ID is required")
    private UUID batchRequestId;
}
