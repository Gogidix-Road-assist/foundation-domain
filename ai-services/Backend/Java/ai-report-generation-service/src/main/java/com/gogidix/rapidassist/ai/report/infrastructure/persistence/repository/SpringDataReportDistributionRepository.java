package com.gogidix.rapidassist.ai.report.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.report.domain.model.DistributionType;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportDistributionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ReportDistributionEntity.
 */
@Repository
public interface SpringDataReportDistributionRepository extends MongoRepository<ReportDistributionEntity, String> {

    /**
     * Find distribution by UUID and tenant.
     */
    Optional<ReportDistributionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all distributions for a report generation.
     */
    List<ReportDistributionEntity> findByReportGenerationIdAndTenantId(UUID reportGenerationId, String tenantId);

    /**
     * Find distributions by type and tenant.
     */
    List<ReportDistributionEntity> findByDistributionTypeAndTenantId(DistributionType distributionType, String tenantId);

    /**
     * Find failed distributions for retry (isSuccessful = false and retryCount < max).
     */
    @Query("{ 'tenantId': ?0, 'isSuccessful': false, 'retryCount': { $lt: ?1 } }")
    List<ReportDistributionEntity> findFailedDistributionsForRetry(String tenantId, int maxRetryCount);

    /**
     * Delete distributions by report generation ID and tenant.
     */
    void deleteByReportGenerationIdAndTenantId(UUID reportGenerationId, String tenantId);

    /**
     * Count distributions by report generation ID and tenant.
     */
    long countByReportGenerationIdAndTenantId(UUID reportGenerationId, String tenantId);

    /**
     * Count successful distributions for a report.
     */
    long countByReportGenerationIdAndTenantIdAndIsSuccessfulTrue(UUID reportGenerationId, String tenantId);
}
