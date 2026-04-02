package com.gogidix.rapidassist.dashboard.reporting.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for report definitions
 */
public class ReportDefinition {

    private final String id;
    private final String tenantId;
    private final String reportId;
    private final String name;
    private final String description;
    private final ReportType reportType;
    private final ReportSource source;
    private final ReportSchedule schedule;
    private final ReportFormat format;
    private final ReportParameters parameters;
    private final ReportTemplate template;
    private final ReportRecipients recipients;
    private final boolean active;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    public enum ReportType {
        DASHBOARD_SNAPSHOT,
        USAGE_ANALYTICS,
        PERFORMANCE_REPORT,
        CUSTOM_ANALYTICS,
        SUMMARY_REPORT
    }

    public enum ReportFormat {
        PDF,
        EXCEL,
        CSV,
        HTML,
        JSON
    }

    public record ReportSource(
        String dashboardId,
        List<String> widgetIds,
        String timeRange,
        Map<String, Object> filters
    ) {
        public static ReportSource defaults() {
            return new ReportSource("", List.of(), "LAST_7_DAYS", Map.of());
        }
    }

    public record ReportSchedule(
        String frequency,  // HOURLY, DAILY, WEEKLY, MONTHLY, ON_DEMAND
        String cronExpression,
        String timezone,
        boolean enabled
    ) {
        public static ReportSchedule onDemand() {
            return new ReportSchedule("ON_DEMAND", "", "UTC", false);
        }
    }

    public record ReportParameters(
        Map<String, Object> customParameters,
        boolean includeCharts,
        boolean includeTables,
        boolean includeSummary,
        int maxDataPoints
    ) {
        public static ReportParameters defaults() {
            return new ReportParameters(Map.of(), true, true, true, 1000);
        }
    }

    public record ReportTemplate(
        String templateId,
        String theme,
        String layout,
        Map<String, String> customStyles
    ) {
        public static ReportTemplate defaults() {
            return new ReportTemplate("default", "light", "standard", Map.of());
        }
    }

    public record ReportRecipients(
        List<String> emails,
        List<String> roles,
        boolean notifyOnCompletion
    ) {
        public static ReportRecipients defaults() {
            return new ReportRecipients(List.of(), List.of(), false);
        }
    }

    private ReportDefinition(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.reportId = builder.reportId;
        this.name = builder.name;
        this.description = builder.description;
        this.reportType = builder.reportType;
        this.source = builder.source;
        this.schedule = builder.schedule;
        this.format = builder.format;
        this.parameters = builder.parameters;
        this.template = builder.template;
        this.recipients = builder.recipients;
        this.active = builder.active;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String reportId() { return reportId; }
    public String name() { return name; }
    public String description() { return description; }
    public ReportType reportType() { return reportType; }
    public ReportSource source() { return source; }
    public ReportSchedule schedule() { return schedule; }
    public ReportFormat format() { return format; }
    public ReportParameters parameters() { return parameters; }
    public ReportTemplate template() { return template; }
    public ReportRecipients recipients() { return recipients; }
    public boolean active() { return active; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String reportId;
        private String name;
        private String description;
        private ReportType reportType;
        private ReportSource source = ReportSource.defaults();
        private ReportSchedule schedule = ReportSchedule.onDemand();
        private ReportFormat format = ReportFormat.PDF;
        private ReportParameters parameters = ReportParameters.defaults();
        private ReportTemplate template = ReportTemplate.defaults();
        private ReportRecipients recipients = ReportRecipients.defaults();
        private boolean active = true;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder reportId(String reportId) { this.reportId = reportId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder reportType(ReportType reportType) { this.reportType = reportType; return this; }
        public Builder source(ReportSource source) { this.source = source; return this; }
        public Builder schedule(ReportSchedule schedule) { this.schedule = schedule; return this; }
        public Builder format(ReportFormat format) { this.format = format; return this; }
        public Builder parameters(ReportParameters parameters) { this.parameters = parameters; return this; }
        public Builder template(ReportTemplate template) { this.template = template; return this; }
        public Builder recipients(ReportRecipients recipients) { this.recipients = recipients; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public ReportDefinition build() {
            return new ReportDefinition(this);
        }
    }
}
