package com.gogidix.rapidassist.analytics.domain.port.out;

import com.gogidix.rapidassist.analytics.domain.model.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Report operations.
 * Following hexagonal architecture principles.
 */
public interface ReportRepositoryPort {

    Report save(String tenantId, Report report);

    Optional<Report> findById(String tenantId, UUID id);

    List<Report> findByTenantId(String tenantId);

    List<Report> findByTenantIdAndReportType(String tenantId, String reportType);

    List<Report> findByTenantIdAndStatus(String tenantId, String status);

    List<Report> findByTenantIdAndCreatedBy(String tenantId, String createdBy);

    List<Report> findByTenantIdAndTimeRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    List<Report> findScheduledReports(String tenantId);

    boolean exists(String tenantId, UUID id);

    void delete(String tenantId, UUID id);
}
