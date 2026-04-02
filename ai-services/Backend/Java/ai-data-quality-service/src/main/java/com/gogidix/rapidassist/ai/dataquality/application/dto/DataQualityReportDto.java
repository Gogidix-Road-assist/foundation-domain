package com.gogidix.rapidassist.ai.dataquality.application.dto;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityReport;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for DataQualityReport
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityReportDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String reportName;
    private String reportType;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private DataQualityReport.ReportStatus status;
    private int totalChecks;
    private int passedChecks;
    private int failedChecks;
    private int criticalIssues;
    private int highIssues;
    private int mediumIssues;
    private int lowIssues;
    private double overallQualityScore;
    private Map<String, Object> summaryMetrics;
    private Map<String, Integer> issuesByEntityType;
    private Map<String, Integer> issuesByRuleType;
    private Map<String, Double> qualityScoresByEntity;
    private String generatedBy;
    private LocalDateTime generatedAt;
    private Map<String, Object> metadata;
}
