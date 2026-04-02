package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * REST request to generate a data quality report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateReportRequest {

    @NotBlank(message = "Report name is required")
    private String reportName;

    @NotBlank(message = "Report type is required")
    private String reportType;

    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private Map<String, Object> filters;
    private String generatedBy;
}
