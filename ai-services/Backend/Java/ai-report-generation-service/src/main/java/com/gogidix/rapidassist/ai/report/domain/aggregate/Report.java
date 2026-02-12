package com.gogidix.rapidassist.ai.report.domain.aggregate;

import com.gogidix.rapidassist.ai.report.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Aggregate Root for Report.
 * Manages the lifecycle and business logic of report generation.
 * Contains: ReportGeneration, ReportTemplate, ReportSchedule as related entities.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private ReportStatus status;
    private ReportFormat format;
    private Map<String, Object> parameters;
    private String fileLocation;
    private Long fileSizeBytes;
    private Integer recordCount;
    private String requestedBy;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    // Related entities
    private ReportGeneration generation;
    private ReportTemplate template;
    private ReportSchedule schedule;
    @Builder.Default
    private List<ReportDistribution> distributions = new ArrayList<>();

    /**
     * Business logic: Initialize a new report generation request
     */
    public static Report initialize(String tenantId, String name, ReportFormat format, String requestedBy) {
        return Report.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .name(name)
                .status(ReportStatus.PENDING)
                .format(format)
                .requestedBy(requestedBy)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .distributions(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Start report generation
     */
    public void startGeneration() {
        if (this.status != ReportStatus.PENDING && this.status != ReportStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot start report generation in status: " + this.status);
        }
        this.status = ReportStatus.IN_PROGRESS;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete report generation
     */
    public void completeGeneration(String fileLocation, Long fileSizeBytes, Integer recordCount) {
        if (this.status != ReportStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete report in status: " + this.status);
        }
        this.status = ReportStatus.COMPLETED;
        this.fileLocation = fileLocation;
        this.fileSizeBytes = fileSizeBytes;
        this.recordCount = recordCount;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Fail report generation
     */
    public void failGeneration(String errorMessage) {
        if (this.status != ReportStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot fail report in status: " + this.status);
        }
        this.status = ReportStatus.FAILED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Cancel report generation
     */
    public void cancel() {
        if (this.status == ReportStatus.COMPLETED || this.status == ReportStatus.FAILED) {
            throw new IllegalStateException("Cannot cancel report in status: " + this.status);
        }
        this.status = ReportStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Start distribution
     */
    public void startDistribution() {
        if (this.status != ReportStatus.COMPLETED) {
            throw new IllegalStateException("Cannot distribute report in status: " + this.status);
        }
        this.status = ReportStatus.DISTRIBUTING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Complete distribution
     */
    public void completeDistribution() {
        if (this.status != ReportStatus.DISTRIBUTING) {
            throw new IllegalStateException("Cannot complete distribution in status: " + this.status);
        }
        this.status = ReportStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Add distribution record
     */
    public void addDistribution(ReportDistribution distribution) {
        distribution.setTenantId(this.tenantId);
        distribution.setReportGenerationId(this.id);
        this.distributions.add(distribution);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Archive report
     */
    public void archive() {
        if (this.status != ReportStatus.COMPLETED) {
            throw new IllegalStateException("Cannot archive report in status: " + this.status);
        }
        this.status = ReportStatus.ARCHIVED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Business logic: Check if report can be cancelled
     */
    public boolean canBeCancelled() {
        return this.status == ReportStatus.PENDING ||
               this.status == ReportStatus.SCHEDULED ||
               this.status == ReportStatus.IN_PROGRESS;
    }

    /**
     * Business logic: Check if report can be distributed
     */
    public boolean canBeDistributed() {
        return this.status == ReportStatus.COMPLETED;
    }

    /**
     * Business logic: Check if report generation can be retried
     */
    public boolean canRetry() {
        return this.status == ReportStatus.FAILED;
    }

    /**
     * Business logic: Get generation duration in seconds
     */
    public long getGenerationDurationSeconds() {
        if (createdAt == null || completedAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, completedAt).getSeconds();
    }

    /**
     * Business logic: Check if report is ready for download
     */
    public boolean isReadyForDownload() {
        return this.status == ReportStatus.COMPLETED && fileLocation != null;
    }

    /**
     * Business logic: Get successful distribution count
     */
    public long getSuccessfulDistributionCount() {
        return this.distributions.stream()
                .filter(ReportDistribution::isSuccessful)
                .count();
    }

    /**
     * Business logic: Get failed distribution count
     */
    public long getFailedDistributionCount() {
        return this.distributions.stream()
                .filter(d -> !d.isSuccessful())
                .count();
    }
}
