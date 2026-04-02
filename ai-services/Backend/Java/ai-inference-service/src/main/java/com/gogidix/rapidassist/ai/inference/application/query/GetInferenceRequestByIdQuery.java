package com.gogidix.rapidassist.ai.inference.application.query;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Query to get an inference request by ID
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetInferenceRequestByIdQuery {

    @NotNull(message = "Inference request ID is required")
    private UUID inferenceRequestId;
}
