package com.gogidix.rapidassist.ai.report.interfaces.rest.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * REST request to generate a report.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {

    private Map<String, Object> parameters;

    @NotBlank(message = "Requested by is required")
    private String requestedBy;
}
