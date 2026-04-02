package com.gogidix.rapidassist.ai.report.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a report schedule.
 * Schedules define when and how often reports should be generated.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportSchedule {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private UUID templateId;
    private ScheduleFrequency frequency;
    private String cronExpression;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<String, Object> parameters;
    private ReportFormat outputFormat;
    private boolean isActive;
    private LocalDateTime nextRunTime;
    private LocalDateTime lastRunTime;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long totalRuns;
    private Long successfulRuns;
    private Long failedRuns;
}
