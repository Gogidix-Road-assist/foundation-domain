package com.gogidix.rapidassist.ai.report.domain.repository;

import com.gogidix.rapidassist.ai.report.domain.model.ReportDistribution;
import com.gogidix.rapidassist.ai.report.domain.model.DistributionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for ReportDistribution.
 */
public interface ReportDistributionRepositoryPort {

    /**
     * Save a distribution record.
     */
    ReportDistribution save(String tenantId, ReportDistribution distribution);

    /**
     * Find a distribution by ID and tenant.
     */
    Optional<ReportDistribution> findById(String tenantId, UUID distributionId);

    /**
     * Find all distributions for a report generation.
     */
    List<ReportDistribution> findByReportGenerationId(String tenantId, UUID reportGenerationId);

    /**
     * Find distributions by type and tenant.
     */
    List<ReportDistribution> findByDistributionType(String tenantId, DistributionType distributionType);

    /**
     * Find failed distributions for retry.
     */
    List<ReportDistribution> findFailedDistributionsForRetry(String tenantId, int maxRetryCount);

    /**
     * Delete distributions by report generation ID.
     */
    void deleteByReportGenerationId(String tenantId, UUID reportGenerationId);

    /**
     * Count distributions by report generation ID.
     */
    long countByReportGenerationId(String tenantId, UUID reportGenerationId);
}
