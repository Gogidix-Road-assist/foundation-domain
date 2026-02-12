package com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.report.domain.model.ReportFormat;
import com.gogidix.rapidassist.ai.report.domain.model.ScheduleFrequency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for ReportSchedule.
 * Maps to report_schedule collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "report_schedule")
public class ReportScheduleEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private UUID templateId;

    @Indexed
    private ScheduleFrequency frequency;

    private String cronExpression;

    @Indexed
    private LocalDateTime startDate;

    @Indexed
    private LocalDateTime endDate;

    private Map<String, Object> parameters;

    private ReportFormat outputFormat;

    @Indexed
    private Boolean isActive;

    @Indexed
    private LocalDateTime nextRunTime;

    @Indexed
    private LocalDateTime lastRunTime;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private Long totalRuns;

    private Long successfulRuns;

    private Long failedRuns;
}
