package com.gogidix.rapidassist.dashboard.reporting.service.domain.model;

import java.time.Instant;
import java.util.*;

/**
 * Domain model for report execution records
 */
public class ReportExecution {

    private final String id;
    private final String tenantId;
    private final String reportId;
    private final String executionId;
    private final ExecutionStatus status;
    private final ReportDefinition.ReportFormat format;
    private final ReportResult result;
    private final String requestedBy;
    private final Instant requestedAt;
    private final Instant startedAt;
    private final Instant completedAt;
    private final String errorMessage;
    private final ExecutionMetrics metrics;

    public enum ExecutionStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public record ReportResult(
        String fileUrl,
        long fileSizeBytes,
        int pageCount,
        String checksum
    ) {
        public static ReportResult empty() {
            return new ReportResult("", 0, 0, "");
        }
    }

    public record ExecutionMetrics(
        long processingTimeMs,
        long dataFetchTimeMs,
        long generationTimeMs,
        int dataPointsProcessed
    ) {
        public static ExecutionMetrics empty() {
            return new ExecutionMetrics(0, 0, 0, 0);
        }
    }

    private ReportExecution(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.reportId = builder.reportId;
        this.executionId = builder.executionId;
        this.status = builder.status;
        this.format = builder.format;
        this.result = builder.result;
        this.requestedBy = builder.requestedBy;
        this.requestedAt = builder.requestedAt != null ? builder.requestedAt : Instant.now();
        this.startedAt = builder.startedAt;
        this.completedAt = builder.completedAt;
        this.errorMessage = builder.errorMessage;
        this.metrics = builder.metrics;
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean isCompleted() {
        return status == ExecutionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == ExecutionStatus.FAILED;
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String reportId() { return reportId; }
    public String executionId() { return executionId; }
    public ExecutionStatus status() { return status; }
    public ReportDefinition.ReportFormat format() { return format; }
    public ReportResult result() { return result; }
    public String requestedBy() { return requestedBy; }
    public Instant requestedAt() { return requestedAt; }
    public Instant startedAt() { return startedAt; }
    public Instant completedAt() { return completedAt; }
    public String errorMessage() { return errorMessage; }
    public ExecutionMetrics metrics() { return metrics; }

    public static class Builder {
        private String id;
        private String tenantId;
        private String reportId;
        private String executionId;
        private ExecutionStatus status = ExecutionStatus.PENDING;
        private ReportDefinition.ReportFormat format = ReportDefinition.ReportFormat.PDF;
        private ReportResult result = ReportResult.empty();
        private String requestedBy;
        private Instant requestedAt;
        private Instant startedAt;
        private Instant completedAt;
        private String errorMessage;
        private ExecutionMetrics metrics = ExecutionMetrics.empty();

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder reportId(String reportId) { this.reportId = reportId; return this; }
        public Builder executionId(String executionId) { this.executionId = executionId; return this; }
        public Builder status(ExecutionStatus status) { this.status = status; return this; }
        public Builder format(ReportDefinition.ReportFormat format) { this.format = format; return this; }
        public Builder result(ReportResult result) { this.result = result; return this; }
        public Builder requestedBy(String requestedBy) { this.requestedBy = requestedBy; return this; }
        public Builder requestedAt(Instant requestedAt) { this.requestedAt = requestedAt; return this; }
        public Builder startedAt(Instant startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(Instant completedAt) { this.completedAt = completedAt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder metrics(ExecutionMetrics metrics) { this.metrics = metrics; return this; }

        public ReportExecution build() {
            return new ReportExecution(this);
        }
    }
}
