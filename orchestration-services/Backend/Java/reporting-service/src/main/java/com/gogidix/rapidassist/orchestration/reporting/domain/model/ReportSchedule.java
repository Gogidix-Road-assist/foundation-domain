package com.gogidix.rapidassist.orchestration.reporting.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * ReportSchedule entity representing automated report generation schedules.
 * Supports cron-based and interval-based scheduling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "report_schedules")
@CompoundIndex(name = "tenant_template_idx", def = "{'tenantId': 1, 'templateId': 1}")
public class ReportSchedule {

    @Id
    private String id;

    @Indexed(unique = true)
    private String scheduleId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private String templateId;

    @Indexed
    private ScheduleStatus status;

    @Indexed
    private ScheduleType scheduleType;

    private String cronExpression; // For CRON-based schedules

    private Integer intervalMinutes; // For interval-based schedules

    private Map<String, Object> parameters;

    @Indexed
    private Report.ReportType reportType;

    @Indexed
    private Report.OutputFormat outputFormat;

    private LocalDateTime nextRunTime;

    private LocalDateTime lastRunTime;

    @Indexed
    private Integer runCount;

    @Indexed
    @Builder.Default
    private Boolean retainHistory = true;

    private Integer retentionDays;

    private Map<String, Object> notificationSettings;

    @Indexed
    private String createdBy;

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
     * Domain logic: Check if schedule is active
     */
    public boolean isActive() {
        return status == ScheduleStatus.ACTIVE;
    }

    /**
     * Domain logic: Check if schedule should run now
     */
    public boolean shouldRun() {
        return isActive() && nextRunTime != null && LocalDateTime.now().isAfter(nextRunTime);
    }

    /**
     * Domain logic: Calculate next run time based on schedule type
     */
    public void calculateNextRunTime() {
        if (scheduleType == ScheduleType.INTERVAL && intervalMinutes != null) {
            this.nextRunTime = LocalDateTime.now().plusMinutes(intervalMinutes);
        }
        // For CRON schedules, this would be calculated by the scheduling framework
    }

    /**
     * Domain logic: Update after successful run
     */
    public void updateAfterRun() {
        this.lastRunTime = LocalDateTime.now();
        this.runCount = (runCount == null ? 0 : runCount) + 1;
        calculateNextRunTime();
    }

    /**
     * Domain logic: Activate schedule
     */
    public void activate() {
        this.status = ScheduleStatus.ACTIVE;
        if (this.nextRunTime == null) {
            calculateNextRunTime();
        }
    }

    /**
     * Domain logic: Deactivate schedule
     */
    public void deactivate() {
        this.status = ScheduleStatus.INACTIVE;
    }

    /**
     * Domain logic: Pause schedule
     */
    public void pause() {
        this.status = ScheduleStatus.PAUSED;
    }

    /**
     * Domain logic: Validate schedule structure
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Schedule name cannot be blank");
        }
        if (templateId == null || templateId.isBlank()) {
            throw new IllegalArgumentException("Template ID is required");
        }
        if (scheduleType == null) {
            throw new IllegalArgumentException("Schedule type is required");
        }
        if (scheduleType == ScheduleType.CRON && (cronExpression == null || cronExpression.isBlank())) {
            throw new IllegalArgumentException("Cron expression is required for CRON schedules");
        }
        if (scheduleType == ScheduleType.INTERVAL && intervalMinutes == null) {
            throw new IllegalArgumentException("Interval minutes is required for INTERVAL schedules");
        }
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID is required");
        }
    }

    /**
     * Domain logic: Soft delete schedule
     */
    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
        this.status = ScheduleStatus.INACTIVE;
    }

    public enum ScheduleStatus {
        ACTIVE,
        INACTIVE,
        PAUSED,
        COMPLETED,
        FAILED
    }

    public enum ScheduleType {
        CRON,
        INTERVAL
    }
}
