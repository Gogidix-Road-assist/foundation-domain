package com.gogidix.rapidassist.orchestration.reporting.domain.port.out;

import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for Report repository operations.
 * Defines the contract for report persistence.
 */
public interface ReportRepositoryPort {

    Report save(Report report);

    Optional<Report> findByReportId(String reportId);

    List<Report> findByTenantId(String tenantId);

    List<Report> findByTenantIdAndReportType(String tenantId, Report.ReportType reportType);

    List<Report> findByTenantIdAndStatus(String tenantId, Report.ReportStatus status);

    List<Report> findByScheduleId(String scheduleId);

    List<Report> findByTemplateId(String templateId);

    List<Report> findByTenantIdAndGeneratedBy(String tenantId, String userId);

    List<Report> findActiveReportsByTenant(String tenantId);

    List<Report> findExpiredReports(LocalDateTime now);

    List<Report> findReportsToDelete(LocalDateTime cutoffDate);

    void deleteByReportId(String reportId);

    void deleteAllByTenantId(String tenantId);

    boolean existsByReportId(String reportId);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, Report.ReportStatus status);
}
