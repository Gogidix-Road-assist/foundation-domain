package com.gogidix.rapidassist.orchestration.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Report entity representing generated reports.
 * Supports various report types and output formats.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reports")
public class Report {

    @Id
    private String id;

    @Indexed(unique = true)
    private String reportId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private ReportType reportType;

    @Indexed
    private OutputFormat outputFormat;

    @Indexed
    private ReportStatus status;

    @Indexed
    private String templateId;

    private Map<String, Object> parameters;

    private String filePath;

    private Long fileSizeBytes;

    private Integer recordCount;

    private LocalDateTime generatedAt;

    private LocalDateTime expiresAt;

    @Indexed
    @Builder.Default
    private Boolean isScheduled = false;

    @Indexed
    private String scheduleId;

    @Indexed
    private String generatedBy;

    private Map<String, Object> metadata;

    @Indexed
    private String tenantId;

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    /**
     * Domain logic: Check if report is ready for download
     */
    public boolean isReady() {
        return status == ReportStatus.COMPLETED && filePath != null;
    }

    /**
     * Domain logic: Check if report has expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Domain logic: Mark report as completed
     */
    public void markAsCompleted(String filePath, Long fileSize, Integer recordCount) {
        this.status = ReportStatus.COMPLETED;
        this.filePath = filePath;
        this.fileSizeBytes = fileSize;
        this.recordCount = recordCount;
        this.generatedAt = LocalDateTime.now();
    }

    /**
     * Domain logic: Mark report as failed
     */
    public void markAsFailed(String errorMessage) {
        this.status = ReportStatus.FAILED;
        if (this.metadata == null) {
            this.metadata = Map.of();
        }
        this.metadata = Map.of("error", errorMessage, "failedAt", LocalDateTime.now().toString());
    }

    /**
     * Domain logic: Validate report structure
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Report name cannot be blank");
        }
        if (reportType == null) {
            throw new IllegalArgumentException("Report type is required");
        }
        if (outputFormat == null) {
            throw new IllegalArgumentException("Output format is required");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
    }

    /**
     * Domain logic: Soft delete report
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }

    public enum ReportType {
        DISPATCH,
        FLEET_UTILIZATION,
        PERFORMANCE,
        COMPLIANCE,
        MAINTENANCE,
        FINANCIAL,
        CUSTOMER_SATISFACTION,
        CUSTOM
    }

    public enum OutputFormat {
        PDF,
        EXCEL,
        CSV,
        HTML,
        JSON
    }

    public enum ReportStatus {
        PENDING,
        GENERATING,
        COMPLETED,
        FAILED,
        EXPIRED
    }
}
