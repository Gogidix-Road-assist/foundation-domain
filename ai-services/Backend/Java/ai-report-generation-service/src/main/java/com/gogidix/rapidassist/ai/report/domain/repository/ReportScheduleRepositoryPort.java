package com.gogidix.rapidassist.ai.report.domain.repository;

import com.gogidix.rapidassist.ai.report.domain.model.ReportSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for ReportSchedule.
 */
public interface ReportScheduleRepositoryPort {

    /**
     * Save a schedule (create or update).
     */
    ReportSchedule save(String tenantId, ReportSchedule schedule);

    /**
     * Find a schedule by ID and tenant.
     */
    Optional<ReportSchedule> findById(String tenantId, UUID scheduleId);

    /**
     * Find all schedules for a tenant.
     */
    List<ReportSchedule> findByTenantId(String tenantId);

    /**
     * Find active schedules for a tenant.
     */
    List<ReportSchedule> findActiveSchedules(String tenantId);

    /**
     * Find schedules due for execution.
     */
    List<ReportSchedule> findSchedulesDueForExecution(LocalDateTime currentTime);

    /**
     * Find schedules by template ID and tenant.
     */
    List<ReportSchedule> findByTemplateId(String tenantId, UUID templateId);

    /**
     * Delete a schedule by ID and tenant.
     */
    void delete(String tenantId, UUID scheduleId);

    /**
     * Check if a schedule exists.
     */
    boolean exists(String tenantId, UUID scheduleId);

    /**
     * Count schedules by tenant.
     */
    long countByTenantId(String tenantId);
}
