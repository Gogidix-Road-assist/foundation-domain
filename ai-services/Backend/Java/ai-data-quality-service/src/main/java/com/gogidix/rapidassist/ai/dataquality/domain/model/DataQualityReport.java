package com.gogidix.rapidassist.ai.dataquality.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain Model representing a data quality report.
 * Aggregates check results into comprehensive reports.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQualityReport {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String reportName;
    private String reportType;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private ReportStatus status;
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

    public enum ReportStatus {
        GENERATING,
        COMPLETED,
        FAILED
    }

    /**
     * Business logic: Start report generation
     */
    public void startGeneration() {
        this.status = ReportStatus.GENERATING;
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete report generation
     */
    public void complete() {
        this.status = ReportStatus.COMPLETED;
        this.calculateOverallScore();
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Fail report generation
     */
    public void fail() {
        this.status = ReportStatus.FAILED;
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Calculate overall quality score
     */
    private void calculateOverallScore() {
        if (totalChecks == 0) {
            this.overallQualityScore = 0.0;
        } else {
            this.overallQualityScore = (passedChecks * 100.0) / totalChecks;
        }
    }

    /**
     * Business logic: Update check statistics
     */
    public void updateStatistics(int total, int passed, int failed) {
        this.totalChecks = total;
        this.passedChecks = passed;
        this.failedChecks = failed;
    }

    /**
     * Business logic: Update issue counts
     */
    public void updateIssueCounts(int critical, int high, int medium, int low) {
        this.criticalIssues = critical;
        this.highIssues = high;
        this.mediumIssues = medium;
        this.lowIssues = low;
    }

    /**
     * Business logic: Get total issue count
     */
    public int getTotalIssues() {
        return criticalIssues + highIssues + mediumIssues + lowIssues;
    }

    /**
     * Business logic: Check if report has critical issues
     */
    public boolean hasCriticalIssues() {
        return criticalIssues > 0;
    }

    /**
     * Business logic: Get quality grade
     */
    public String getQualityGrade() {
        if (overallQualityScore >= 95) return "A";
        if (overallQualityScore >= 85) return "B";
        if (overallQualityScore >= 70) return "C";
        if (overallQualityScore >= 50) return "D";
        return "F";
    }

    /**
     * Business logic: Check if quality is acceptable
     */
    public boolean isQualityAcceptable(double threshold) {
        return overallQualityScore >= threshold;
    }
}
