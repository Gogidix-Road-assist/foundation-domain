package com.gogidix.rapidassist.orchestration.reporting.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for ReportSchedule responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportScheduleDTO {
    private String id;
    private String scheduleId;
    private String name;
    private String description;
    private String templateId;
    private ScheduleStatusDTO status;
    private ScheduleTypeDTO scheduleType;
    private String cronExpression;
    private Integer intervalMinutes;
    private Map<String, Object> parameters;
    private ReportDTO.ReportTypeDTO reportType;
    private ReportDTO.OutputFormatDTO outputFormat;
    private LocalDateTime nextRunTime;
    private LocalDateTime lastRunTime;
    private Integer runCount;
    private Boolean retainHistory;
    private Integer retentionDays;
    private Map<String, Object> notificationSettings;
    private String createdBy;
    private String tenantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ScheduleStatusDTO {
        ACTIVE, INACTIVE, PAUSED, COMPLETED, FAILED
    }

    public enum ScheduleTypeDTO {
        CRON, INTERVAL
    }
}
