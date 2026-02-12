package com.gogidix.rapidassist.orchestration.reporting.domain.port.in;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Input port for Report Schedule operations.
 * Defines the contract for schedule management use cases.
 */
public interface ReportSchedulePort {

    /**
     * Create a new schedule
     */
    ReportSchedule createSchedule(ReportSchedule schedule);

    /**
     * Update schedule
     */
    ReportSchedule updateSchedule(String scheduleId, ReportSchedule schedule);

    /**
     * Get schedule by ID
     */
    Optional<ReportSchedule> getSchedule(String scheduleId);

    /**
     * Get schedules by tenant
     */
    List<ReportSchedule> getSchedulesByTenant(String tenantId);

    /**
     * Get active schedules
     */
    List<ReportSchedule> getActiveSchedules(String tenantId);

    /**
     * Get schedules by template
     */
    List<ReportSchedule> getSchedulesByTemplate(String templateId);

    /**
     * Get schedules by user
     */
    List<ReportSchedule> getSchedulesByUser(String createdBy);

    /**
     * Delete schedule
     */
    void deleteSchedule(String scheduleId);

    /**
     * Activate schedule
     */
    void activateSchedule(String scheduleId);

    /**
     * Deactivate schedule
     */
    void deactivateSchedule(String scheduleId);

    /**
     * Pause schedule
     */
    void pauseSchedule(String scheduleId);

    /**
     * Resume schedule
     */
    void resumeSchedule(String scheduleId);

    /**
     * Validate schedule
     */
    boolean validateSchedule(ReportSchedule schedule);

    /**
     * Trigger schedule manually
     */
    void triggerSchedule(String scheduleId);

    /**
     * Get due schedules
     */
    List<ReportSchedule> getDueSchedules();

    /**
     * Update schedule after run
     */
    void updateScheduleAfterRun(String scheduleId);

    /**
     * Get schedule statistics
     */
    ScheduleStatistics getScheduleStatistics(String scheduleId);

    record ScheduleStatistics(
        long totalRuns,
        long successfulRuns,
        long failedRuns,
        LocalDateTime lastRun,
        LocalDateTime nextRun
    ) {}
}
