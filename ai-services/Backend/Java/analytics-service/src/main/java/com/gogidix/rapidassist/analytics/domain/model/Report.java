package com.gogidix.rapidassist.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a report.
 * Contains generated analytics reports with visualizations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    private UUID id;
    private String tenantId;
    private String reportName;
    private String reportType;
    private String reportCategory;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> reportData;
    private List<String> includedMetrics;
    private List<ChartConfiguration> charts;
    private String format;
    private String status;
    private Integer totalViews;
    private LocalDateTime lastViewedAt;
    private String createdBy;
    private String lastModifiedBy;
    private String description;
    private String schedule;
    private List<String> recipients;
    private Map<String, Object> filters;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Nested class for chart configuration
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChartConfiguration {
        private String chartType;
        private String title;
        private String xAxis;
        private String yAxis;
        private Map<String, Object> configuration;
        private Integer order;
    }

    /**
     * Business logic: Check if report is scheduled
     */
    public boolean isScheduled() {
        return schedule != null && !schedule.isEmpty();
    }

    /**
     * Business logic: Check if report is ready for generation
     */
    public boolean isReadyForGeneration() {
        return "PENDING".equals(status) && startDate != null && endDate != null;
    }

    /**
     * Business logic: Mark report as generated
     */
    public void markAsGenerated(String generatedBy) {
        this.status = "COMPLETED";
        this.updatedAt = LocalDateTime.now();
        this.lastModifiedBy = generatedBy;
    }

    /**
     * Business logic: Increment view count
     */
    public void incrementViewCount() {
        this.totalViews = (this.totalViews == null ? 0 : this.totalViews) + 1;
        this.lastViewedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if report is expired
     */
    public boolean isExpired() {
        if (endDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(endDate.plusMonths(1));
    }

    /**
     * Business logic: Get report age in days
     */
    public long getAgeInDays() {
        return createdAt != null ? java.time.Duration.between(createdAt, LocalDateTime.now()).toDays() : 0;
    }
}
