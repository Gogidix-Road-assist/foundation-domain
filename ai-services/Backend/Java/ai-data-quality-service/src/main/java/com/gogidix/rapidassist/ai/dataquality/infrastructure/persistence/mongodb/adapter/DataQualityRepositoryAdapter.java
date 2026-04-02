package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter;

import com.gogidix.rapidassist.ai.dataquality.domain.model.*;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.*;

/**
 * Adapter to convert between domain models and MongoDB entities
 */
public class DataQualityRepositoryAdapter {

    public static DataQualityRuleEntity toEntity(DataQualityRule rule) {
        return DataQualityRuleEntity.builder()
                .id(rule.getId())
                .tenantId(rule.getTenantId())
                .name(rule.getName())
                .description(rule.getDescription())
                .ruleType(rule.getRuleType())
                .entityType(rule.getEntityType())
                .attributeName(rule.getAttributeName())
                .operator(rule.getOperator())
                .thresholdValue(rule.getThresholdValue())
                .parameters(rule.getParameters())
                .severity(rule.getSeverity())
                .active(rule.isActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .createdBy(rule.getCreatedBy())
                .updatedBy(rule.getUpdatedBy())
                .build();
    }

    public static DataQualityRule toDomain(DataQualityRuleEntity entity) {
        return DataQualityRule.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .ruleType(entity.getRuleType())
                .entityType(entity.getEntityType())
                .attributeName(entity.getAttributeName())
                .operator(entity.getOperator())
                .thresholdValue(entity.getThresholdValue())
                .parameters(entity.getParameters())
                .severity(entity.getSeverity())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static DataQualityCheckEntity toEntity(DataQualityCheck check) {
        return DataQualityCheckEntity.builder()
                .id(check.getId())
                .tenantId(check.getTenantId())
                .ruleId(check.getRuleId())
                .checkName(check.getCheckName())
                .entityType(check.getEntityType())
                .datasetIdentifier(check.getDatasetIdentifier())
                .status(check.getStatus())
                .totalRecords(check.getTotalRecords())
                .recordsChecked(check.getRecordsChecked())
                .passedRecords(check.getPassedRecords())
                .failedRecords(check.getFailedRecords())
                .passPercentage(check.getPassPercentage())
                .checkParameters(check.getCheckParameters())
                .executedAt(check.getExecutedAt())
                .completedAt(check.getCompletedAt())
                .executionDurationMs(check.getExecutionDurationMs())
                .errorMessage(check.getErrorMessage())
                .metadata(check.getMetadata())
                .executedBy(check.getExecutedBy())
                .build();
    }

    public static DataQualityCheck toDomain(DataQualityCheckEntity entity) {
        return DataQualityCheck.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .ruleId(entity.getRuleId())
                .checkName(entity.getCheckName())
                .entityType(entity.getEntityType())
                .datasetIdentifier(entity.getDatasetIdentifier())
                .status(entity.getStatus())
                .totalRecords(entity.getTotalRecords())
                .recordsChecked(entity.getRecordsChecked())
                .passedRecords(entity.getPassedRecords())
                .failedRecords(entity.getFailedRecords())
                .passPercentage(entity.getPassPercentage())
                .checkParameters(entity.getCheckParameters())
                .executedAt(entity.getExecutedAt())
                .completedAt(entity.getCompletedAt())
                .executionDurationMs(entity.getExecutionDurationMs())
                .errorMessage(entity.getErrorMessage())
                .metadata(entity.getMetadata())
                .executedBy(entity.getExecutedBy())
                .build();
    }

    public static DataQualityIssueEntity toEntity(DataQualityIssue issue) {
        return DataQualityIssueEntity.builder()
                .id(issue.getId())
                .tenantId(issue.getTenantId())
                .checkId(issue.getCheckId())
                .ruleId(issue.getRuleId())
                .issueType(issue.getIssueType())
                .severity(issue.getSeverity())
                .entityType(issue.getEntityType())
                .entityId(issue.getEntityId())
                .attributeName(issue.getAttributeName())
                .currentValue(issue.getCurrentValue())
                .expectedValue(issue.getExpectedValue())
                .description(issue.getDescription())
                .status(issue.getStatus())
                .detectedAt(issue.getDetectedAt())
                .resolvedAt(issue.getResolvedAt())
                .resolvedBy(issue.getResolvedBy())
                .resolutionNotes(issue.getResolutionNotes())
                .issueDetails(issue.getIssueDetails())
                .occurrenceCount(issue.getOccurrenceCount())
                .affectedBusinessKey(issue.getAffectedBusinessKey())
                .build();
    }

    public static DataQualityIssue toDomain(DataQualityIssueEntity entity) {
        return DataQualityIssue.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .checkId(entity.getCheckId())
                .ruleId(entity.getRuleId())
                .issueType(entity.getIssueType())
                .severity(entity.getSeverity())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .attributeName(entity.getAttributeName())
                .currentValue(entity.getCurrentValue())
                .expectedValue(entity.getExpectedValue())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .detectedAt(entity.getDetectedAt())
                .resolvedAt(entity.getResolvedAt())
                .resolvedBy(entity.getResolvedBy())
                .resolutionNotes(entity.getResolutionNotes())
                .issueDetails(entity.getIssueDetails())
                .occurrenceCount(entity.getOccurrenceCount())
                .affectedBusinessKey(entity.getAffectedBusinessKey())
                .build();
    }

    public static DataQualityReportEntity toEntity(DataQualityReport report) {
        return DataQualityReportEntity.builder()
                .id(report.getId())
                .tenantId(report.getTenantId())
                .reportName(report.getReportName())
                .reportType(report.getReportType())
                .reportPeriodStart(report.getReportPeriodStart())
                .reportPeriodEnd(report.getReportPeriodEnd())
                .status(report.getStatus())
                .totalChecks(report.getTotalChecks())
                .passedChecks(report.getPassedChecks())
                .failedChecks(report.getFailedChecks())
                .criticalIssues(report.getCriticalIssues())
                .highIssues(report.getHighIssues())
                .mediumIssues(report.getMediumIssues())
                .lowIssues(report.getLowIssues())
                .overallQualityScore(report.getOverallQualityScore())
                .summaryMetrics(report.getSummaryMetrics())
                .issuesByEntityType(report.getIssuesByEntityType())
                .issuesByRuleType(report.getIssuesByRuleType())
                .qualityScoresByEntity(report.getQualityScoresByEntity())
                .generatedBy(report.getGeneratedBy())
                .generatedAt(report.getGeneratedAt())
                .metadata(report.getMetadata())
                .build();
    }

    public static DataQualityReport toDomain(DataQualityReportEntity entity) {
        return DataQualityReport.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .reportName(entity.getReportName())
                .reportType(entity.getReportType())
                .reportPeriodStart(entity.getReportPeriodStart())
                .reportPeriodEnd(entity.getReportPeriodEnd())
                .status(entity.getStatus())
                .totalChecks(entity.getTotalChecks())
                .passedChecks(entity.getPassedChecks())
                .failedChecks(entity.getFailedChecks())
                .criticalIssues(entity.getCriticalIssues())
                .highIssues(entity.getHighIssues())
                .mediumIssues(entity.getMediumIssues())
                .lowIssues(entity.getLowIssues())
                .overallQualityScore(entity.getOverallQualityScore())
                .summaryMetrics(entity.getSummaryMetrics())
                .issuesByEntityType(entity.getIssuesByEntityType())
                .issuesByRuleType(entity.getIssuesByRuleType())
                .qualityScoresByEntity(entity.getQualityScoresByEntity())
                .generatedBy(entity.getGeneratedBy())
                .generatedAt(entity.getGeneratedAt())
                .metadata(entity.getMetadata())
                .build();
    }

    public static DataQualityMetricEntity toEntity(DataQualityMetric metric) {
        return DataQualityMetricEntity.builder()
                .id(metric.getId())
                .tenantId(metric.getTenantId())
                .metricName(metric.getMetricName())
                .metricType(metric.getMetricType() != null ? metric.getMetricType().toString() : null)
                .entityType(metric.getEntityType())
                .metricValue(metric.getMetricValue())
                .unit(metric.getUnit())
                .metricTimestamp(metric.getMetricTimestamp())
                .metricDimensions(metric.getMetricDimensions())
                .metadata(metric.getMetadata())
                .calculatedAt(metric.getCalculatedAt())
                .calculatedBy(metric.getCalculatedBy())
                .build();
    }

    public static DataQualityMetric toDomain(DataQualityMetricEntity entity) {
        DataQualityMetric.MetricType metricType = null;
        if (entity.getMetricType() != null) {
            try {
                metricType = DataQualityMetric.MetricType.valueOf(entity.getMetricType());
            } catch (IllegalArgumentException e) {
                // Handle unknown metric types
                metricType = DataQualityMetric.MetricType.CUSTOM;
            }
        }

        return DataQualityMetric.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .metricName(entity.getMetricName())
                .metricType(metricType != null ? metricType.toString() : null)
                .entityType(entity.getEntityType())
                .metricValue(entity.getMetricValue())
                .unit(entity.getUnit())
                .metricTimestamp(entity.getMetricTimestamp())
                .metricDimensions(entity.getMetricDimensions())
                .metadata(entity.getMetadata())
                .calculatedAt(entity.getCalculatedAt())
                .calculatedBy(entity.getCalculatedBy())
                .build();
    }
}
