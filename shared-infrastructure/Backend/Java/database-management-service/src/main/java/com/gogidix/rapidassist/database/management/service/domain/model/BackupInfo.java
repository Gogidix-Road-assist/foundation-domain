package com.gogidix.rapidassist.database.management.service.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model for database backup information.
 * This entity is tenant-scoped to ensure proper multi-tenancy isolation.
 */
@Entity
@Table(name = "backup_info",
    indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_tenant_connection", columnList = "tenant_id,connection_id"),
        @Index(name = "idx_status", columnList = "status")
    }
)
public class BackupInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "connection_id", nullable = false)
    private String connectionId;

    @Column(name = "backup_name", nullable = false)
    private String backupName;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private BackupType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BackupStatus status;

    @Column(name = "storage_location")
    private String storageLocation;

    @Column(name = "size_bytes")
    private long sizeBytes;

    @Column(name = "duration")
    private long duration;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "metadata", length = 2000)
    private String metadataJson;

    @Transient
    private Map<String, Object> metadata;

    @Column(name = "tables_count")
    private int tablesCount;

    @Column(name = "rows_count")
    private int rowsCount;

    public enum BackupType {
        FULL,
        INCREMENTAL,
        DIFFERENTIAL,
        SCHEMA_ONLY
    }

    public enum BackupStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }

    public BackupInfo() {
        this.startTime = LocalDateTime.now();
        this.status = BackupStatus.PENDING;
    }

    public BackupInfo(String id, String tenantId, String connectionId, String backupName, BackupType type) {
        this();
        this.id = id;
        this.tenantId = tenantId;
        this.connectionId = connectionId;
        this.backupName = backupName;
        this.type = type;
    }

    @PrePersist
    protected void onCreate() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        // Update timestamp on modifications
    }

    // Business logic
    public void markAsInProgress() {
        this.status = BackupStatus.IN_PROGRESS;
    }

    public void markAsCompleted(String storageLocation, long sizeBytes, long duration) {
        this.status = BackupStatus.COMPLETED;
        this.storageLocation = storageLocation;
        this.sizeBytes = sizeBytes;
        this.duration = duration;
        this.endTime = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = BackupStatus.FAILED;
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return status == BackupStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == BackupStatus.FAILED;
    }

    public boolean isInProgress() {
        return status == BackupStatus.IN_PROGRESS;
    }

    public String getSizeInMB() {
        return String.format("%.2f MB", sizeBytes / (1024.0 * 1024.0));
    }

    public String getDurationFormatted() {
        if (duration == 0) return "N/A";
        long seconds = duration / 1000;
        if (seconds < 60) return seconds + "s";
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return String.format("%dm %ds", minutes, remainingSeconds);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }

    public String getBackupName() { return backupName; }
    public void setBackupName(String backupName) { this.backupName = backupName; }

    public BackupType getType() { return type; }
    public void setType(BackupType type) { this.type = type; }

    public BackupStatus getStatus() { return status; }
    public void setStatus(BackupStatus status) { this.status = status; }

    public String getStorageLocation() { return storageLocation; }
    public void setStorageLocation(String storageLocation) { this.storageLocation = storageLocation; }

    public long getSizeBytes() { return sizeBytes; }
    public void setSizeBytes(long sizeBytes) { this.sizeBytes = sizeBytes; }

    public long getDuration() { return duration; }
    public void setDuration(long duration) { this.duration = duration; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public int getTablesCount() { return tablesCount; }
    public void setTablesCount(int tablesCount) { this.tablesCount = tablesCount; }

    public int getRowsCount() { return rowsCount; }
    public void setRowsCount(int rowsCount) { this.rowsCount = rowsCount; }

    public String getMetadataJson() { return metadataJson; }
    public void setMetadataJson(String metadataJson) { this.metadataJson = metadataJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BackupInfo backupInfo = (BackupInfo) o;
        return Objects.equals(id, backupInfo.id) && Objects.equals(tenantId, backupInfo.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId);
    }

    @Override
    public String toString() {
        return "BackupInfo{" +
            "id='" + id + '\'' +
            ", tenantId='" + tenantId + '\'' +
            ", connectionId='" + connectionId + '\'' +
            ", backupName='" + backupName + '\'' +
            ", status=" + status +
            '}';
    }
}
