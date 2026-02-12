package com.gogidix.rapidassist.reporting.read.model.service.domain.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

/**
 * Report entity representing a read-only view of aggregated data.
 * <p>
 * CRITICAL: All reports MUST be tenant-isolated to prevent data leakage.
 * Reports are READ-ONLY materialized views that aggregate data across the system.
 * Without tenant filtering, Tenant A could potentially see ALL of Tenant B's data.
 * <p>
 * This is a CATASTROPHIC security vulnerability if not properly isolated.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Entity
@Table(
    name = "reports",
    indexes = {
        @Index(name = "idx_reports_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_reports_report_type", columnList = "report_type"),
        @Index(name = "idx_reports_status", columnList = "status"),
        @Index(name = "idx_reports_created_at", columnList = "created_at"),
        @Index(name = "idx_reports_generated_at", columnList = "generated_at"),
        @Index(name = "idx_reports_tenant_type_status", columnList = "tenant_id, report_type, status")
    }
)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    /**
     * CRITICAL: Tenant ID for multi-tenancy isolation.
     * ALL queries MUST filter by this field to prevent cross-tenant data access.
     * <p>
     * Without this filter, reports from ALL tenants would be accessible,
     * leading to CATASTROPHIC data leakage.
     */
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "generated_by", nullable = false)
    private String generatedBy;

    @Column(name = "parameters", columnDefinition = "jsonb")
    private String parameters;

    @Column(name = "data", columnDefinition = "jsonb")
    private String data;

    @Column(name = "row_count")
    private Long rowCount;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "generated_at")
    private Instant generatedAt;

    @Column(name = "accessed_at")
    private Instant accessedAt;

    @Column(name = "access_count")
    private Integer accessCount = 0;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * Report status enum.
     */
    public enum ReportStatus {
        PENDING,
        GENERATING,
        COMPLETED,
        FAILED,
        EXPIRED,
        ARCHIVED
    }

    // Default constructor required by JPA
    protected Report() {
    }

    // Builder pattern
    private Report(Builder builder) {
        this.id = builder.id;
        this.tenantId = builder.tenantId;
        this.reportType = builder.reportType;
        this.status = builder.status;
        this.title = builder.title;
        this.description = builder.description;
        this.generatedBy = builder.generatedBy;
        this.parameters = builder.parameters;
        this.data = builder.data;
        this.rowCount = builder.rowCount;
        this.filePath = builder.filePath;
        this.fileSizeBytes = builder.fileSizeBytes;
        this.fileFormat = builder.fileFormat;
        this.expiresAt = builder.expiresAt;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.generatedAt = builder.generatedAt;
        this.accessedAt = builder.accessedAt;
        this.accessCount = builder.accessCount != null ? builder.accessCount : 0;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        if (this.status == null) {
            this.status = ReportStatus.PENDING;
        }
        if (this.accessCount == null) {
            this.accessCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Read-only model - updates should be minimal
    }

    public void markAsAccessed() {
        this.accessedAt = Instant.now();
        this.accessCount++;
    }

    public boolean isExpired() {
        return expiresAt != null && Instant.now().isAfter(expiresAt);
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getReportType() {
        return reportType;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getParameters() {
        return parameters;
    }

    public String getData() {
        return data;
    }

    public Long getRowCount() {
        return rowCount;
    }

    public String getFilePath() {
        return filePath;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getGeneratedAt() {
        return generatedAt;
    }

    public Instant getAccessedAt() {
        return accessedAt;
    }

    public Integer getAccessCount() {
        return accessCount;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return Objects.equals(id, report.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Report{" +
                "id='" + id + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", reportType='" + reportType + '\'' +
                ", status=" + status +
                ", title='" + title + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String reportType;
        private ReportStatus status;
        private String title;
        private String description;
        private String generatedBy;
        private String parameters;
        private String data;
        private Long rowCount;
        private String filePath;
        private Long fileSizeBytes;
        private String fileFormat;
        private Instant expiresAt;
        private Instant createdAt;
        private Instant generatedAt;
        private Instant accessedAt;
        private Integer accessCount;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder reportType(String reportType) {
            this.reportType = reportType;
            return this;
        }

        public Builder status(ReportStatus status) {
            this.status = status;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder generatedBy(String generatedBy) {
            this.generatedBy = generatedBy;
            return this;
        }

        public Builder parameters(String parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder data(String data) {
            this.data = data;
            return this;
        }

        public Builder rowCount(Long rowCount) {
            this.rowCount = rowCount;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileSizeBytes(Long fileSizeBytes) {
            this.fileSizeBytes = fileSizeBytes;
            return this;
        }

        public Builder fileFormat(String fileFormat) {
            this.fileFormat = fileFormat;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder generatedAt(Instant generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        public Builder accessedAt(Instant accessedAt) {
            this.accessedAt = accessedAt;
            return this;
        }

        public Builder accessCount(Integer accessCount) {
            this.accessCount = accessCount;
            return this;
        }

        public Report build() {
            if (tenantId == null) {
                throw new IllegalStateException("tenantId is required");
            }
            if (reportType == null) {
                throw new IllegalStateException("reportType is required");
            }
            if (title == null) {
                throw new IllegalStateException("title is required");
            }
            if (generatedBy == null) {
                throw new IllegalStateException("generatedBy is required");
            }
            return new Report(this);
        }
    }
}
