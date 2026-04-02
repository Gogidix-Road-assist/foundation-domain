package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.AnalyticsReport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for AnalyticsReport aggregate
 */
public interface AnalyticsReportRepositoryPort {

    /**
     * Save a report
     */
    AnalyticsReport save(AnalyticsReport report);

    /**
     * Find report by ID
     */
    Optional<AnalyticsReport> findById(UUID id);

    /**
     * Find report by ID and tenant ID
     */
    Optional<AnalyticsReport> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all reports for a tenant
     */
    List<AnalyticsReport> findByTenantId(String tenantId);

    /**
     * Find reports by status
     */
    List<AnalyticsReport> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Find reports by type
     */
    List<AnalyticsReport> findByTenantIdAndReportType(String tenantId, String reportType);

    /**
     * Delete a report
     */
    void deleteById(UUID id);

    /**
     * Check if report exists
     */
    boolean existsById(UUID id);
}
