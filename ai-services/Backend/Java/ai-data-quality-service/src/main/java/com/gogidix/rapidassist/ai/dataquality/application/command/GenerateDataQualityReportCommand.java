package com.gogidix.rapidassist.ai.dataquality.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Command to generate a data quality report
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDataQualityReportCommand {

    private String tenantId;
    private String reportName;
    private String reportType;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private Map<String, Object> filters;
    private String generatedBy;
}
