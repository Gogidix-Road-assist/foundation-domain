package com.gogidix.rapidassist.orchestration.reporting.domain.port.out;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.ReportHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Output port for ReportHistory repository operations.
 * Defines the contract for history persistence.
 */
public interface ReportHistoryRepositoryPort {

    ReportHistory save(ReportHistory history);

    List<ReportHistory> findByReportId(String reportId);

    List<ReportHistory> findByScheduleId(String scheduleId);

    List<ReportHistory> findByTenantId(String tenantId);

    List<ReportHistory> findByTenantIdAndEventType(String tenantId, ReportHistory.HistoryEventType eventType);

    List<ReportHistory> findByTenantIdAndEventStatus(String tenantId, ReportHistory.HistoryEventStatus eventStatus);

    List<ReportHistory> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    List<ReportHistory> findRecentHistoryByTenant(String tenantId, int limit);

    void deleteByHistoryId(String historyId);

    void deleteByReportId(String reportId);

    void deleteByScheduleId(String scheduleId);

    void deleteHistoryBefore(LocalDateTime cutoffDate);

    void deleteAllByTenantId(String tenantId);

    long countByTenantId(String tenantId);

    long countByReportId(String reportId);
}
