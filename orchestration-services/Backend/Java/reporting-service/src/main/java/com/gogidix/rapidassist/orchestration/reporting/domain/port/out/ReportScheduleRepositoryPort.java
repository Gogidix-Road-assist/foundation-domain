package com.gogidix.rapidassist.orchestration.reporting.domain.port.out;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportSchedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for ReportSchedule repository operations.
 * Defines the contract for schedule persistence.
 */
public interface ReportScheduleRepositoryPort {

    ReportSchedule save(ReportSchedule schedule);

    Optional<ReportSchedule> findByScheduleId(String scheduleId);

    List<ReportSchedule> findByTenantId(String tenantId);

    List<ReportSchedule> findByTenantIdAndStatus(String tenantId, ReportSchedule.ScheduleStatus status);

    List<ReportSchedule> findActiveSchedulesByTenant(String tenantId);

    List<ReportSchedule> findByTemplateId(String templateId);

    List<ReportSchedule> findDueSchedules(LocalDateTime now);

    List<ReportSchedule> findByCreatedBy(String createdBy);

    void deleteByScheduleId(String scheduleId);

    void deleteAllByTenantId(String tenantId);

    boolean existsByScheduleId(String scheduleId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, ReportSchedule.ScheduleStatus status);
}
