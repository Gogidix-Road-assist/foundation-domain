package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * REST request to resolve a data quality issue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResolveIssueRequest {

    @NotBlank(message = "Resolved by is required")
    private String resolvedBy;

    private String resolutionNotes;
}
