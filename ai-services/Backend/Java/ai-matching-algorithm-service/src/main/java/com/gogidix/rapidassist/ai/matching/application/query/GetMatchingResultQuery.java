package com.gogidix.rapidassist.ai.matching.application.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Query to get a matching result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMatchingResultQuery {

    @NotBlank(message = "Tenant ID is required")
    private String tenantId;

    @NotNull(message = "Result ID is required")
    private java.util.UUID resultId;
}
