package com.gogidix.rapidassist.ai.report.domain.repository;

import com.gogidix.rapidassist.ai.report.domain.aggregate.Report;
import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving Report aggregates.
 */
public interface ReportRepositoryPort {

    /**
     * Save a report (create or update).
     */
    Report save(String tenantId, Report report);

    /**
     * Find a report by ID and tenant.
     */
    Optional<Report> findById(String tenantId, UUID reportId);

    /**
     * Find all reports for a tenant.
     */
    List<Report> findByTenantId(String tenantId);

    /**
     * Find reports by status and tenant.
     */
    List<Report> findByStatus(String tenantId, ReportStatus status);

    /**
     * Find reports by requested by user and tenant.
     */
    List<Report> findByRequestedBy(String tenantId, String requestedBy);

    /**
     * Find reports by template ID and tenant.
     */
    List<Report> findByTemplateId(String tenantId, UUID templateId);

    /**
     * Find reports by schedule ID and tenant.
     */
    List<Report> findByScheduleId(String tenantId, UUID scheduleId);

    /**
     * Find reports created between dates for a tenant.
     */
    List<Report> findByCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find pending or in-progress reports for a tenant.
     */
    List<Report> findPendingReports(String tenantId);

    /**
     * Find completed reports ready for distribution.
     */
    List<Report> findReportsReadyForDistribution(String tenantId);

    /**
     * Find failed reports that can be retried.
     */
    List<Report> findFailedReportsForRetry(String tenantId, int maxRetryCount);

    /**
     * Delete a report by ID and tenant.
     */
    void delete(String tenantId, UUID reportId);

    /**
     * Check if a report exists.
     */
    boolean exists(String tenantId, UUID reportId);

    /**
     * Count reports by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count reports by status and tenant.
     */
    long countByStatus(String tenantId, ReportStatus status);

    /**
     * Find old completed reports for archival.
     */
    List<Report> findOldCompletedReports(String tenantId, LocalDateTime olderThan);
}
