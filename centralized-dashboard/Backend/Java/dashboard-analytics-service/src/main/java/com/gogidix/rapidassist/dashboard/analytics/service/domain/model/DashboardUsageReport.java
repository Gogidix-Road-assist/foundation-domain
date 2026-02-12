package com.gogidix.rapidassist.dashboard.analytics.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for dashboard usage reports
 */
public class DashboardUsageReport {

    private final String id;
    private final String tenantId;
    private final String dashboardId;
    private final ReportingPeriod period;
    private final UsageStatistics usageStats;
    private final UserEngagementMetrics engagementMetrics;
    private final PerformanceReport performanceReport;
    private final Instant generatedAt;

    public record ReportingPeriod(
        Instant startDate,
        Instant endDate,
        String periodType  // DAILY, WEEKLY, MONTHLY, CUSTOM
    ) {}

    public record UsageStatistics(
        long totalViews,
        long uniqueUsers,
        long totalEdits,
        long totalShares,
        long totalExports,
        long averageSessionDurationMs,
        Map<String, Long> viewsByUser,
        Map<String, Long> viewsByDay
    ) {}

    public record UserEngagementMetrics(
        double averageTimeOnDashboard,
        double bounceRate,
        int mostViewedWidgetsCount,
        List<String> topWidgets,
        Map<String, Integer> widgetInteractionCount
    ) {}

    public record PerformanceReport(
        double averageLoadTimeMs,
        double averageRenderTimeMs,
        double averageDataFetchTimeMs,
        long slowLoadCount,
        long errorCount,
        Map<String, Double> percentileLoadTimes
    ) {}

    private DashboardUsageReport(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.dashboardId = builder.dashboardId;
        this.period = builder.period;
        this.usageStats = builder.usageStats;
        this.engagementMetrics = builder.engagementMetrics;
        this.performanceReport = builder.performanceReport;
        this.generatedAt = builder.generatedAt != null ? builder.generatedAt : Instant.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String dashboardId() { return dashboardId; }
    public ReportingPeriod period() { return period; }
    public UsageStatistics usageStats() { return usageStats; }
    public UserEngagementMetrics engagementMetrics() { return engagementMetrics; }
    public PerformanceReport performanceReport() { return performanceReport; }
    public Instant generatedAt() { return generatedAt; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String dashboardId;
        private ReportingPeriod period;
        private UsageStatistics usageStats;
        private UserEngagementMetrics engagementMetrics;
        private PerformanceReport performanceReport;
        private Instant generatedAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder dashboardId(String dashboardId) { this.dashboardId = dashboardId; return this; }
        public Builder period(ReportingPeriod period) { this.period = period; return this; }
        public Builder usageStats(UsageStatistics usageStats) { this.usageStats = usageStats; return this; }
        public Builder engagementMetrics(UserEngagementMetrics engagementMetrics) { this.engagementMetrics = engagementMetrics; return this; }
        public Builder performanceReport(PerformanceReport performanceReport) { this.performanceReport = performanceReport; return this; }
        public Builder generatedAt(Instant generatedAt) { this.generatedAt = generatedAt; return this; }

        public DashboardUsageReport build() {
            return new DashboardUsageReport(this);
        }
    }
}
