package com.gogidix.rapidassist.ai.report.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.report.domain.model.ReportStatus;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ReportEntity.
 */
@Repository
public interface SpringDataReportRepository extends MongoRepository<ReportEntity, String> {

    /**
     * Find report by UUID and tenant.
     */
    Optional<ReportEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all reports by tenant.
     */
    List<ReportEntity> findByTenantId(String tenantId);

    /**
     * Find reports by status and tenant.
     */
    List<ReportEntity> findByStatusAndTenantId(ReportStatus status, String tenantId);

    /**
     * Find reports by requested by user and tenant.
     */
    List<ReportEntity> findByRequestedByAndTenantId(String requestedBy, String tenantId);

    /**
     * Check if report exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete report by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count reports by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count reports by status and tenant.
     */
    long countByStatusAndTenantId(ReportStatus status, String tenantId);

    /**
     * Find reports by status in list and tenant.
     */
    List<ReportEntity> findByTenantIdAndStatusIn(String tenantId, List<ReportStatus> statuses);

    /**
     * Find reports created between dates for tenant.
     */
    List<ReportEntity> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find old completed reports for archival.
     */
    @Query("{ 'tenantId': ?0, 'status': 'COMPLETED', 'completedAt': { $lt: ?1 } }")
    List<ReportEntity> findOldCompletedReports(String tenantId, LocalDateTime olderThan);

    /**
     * Find failed reports with retry count less than max.
     */
    @Query("{ 'tenantId': ?0, 'status': 'FAILED', 'retryCount': { $lt: ?1 } }")
    List<ReportEntity> findFailedReportsForRetry(String tenantId, int maxRetryCount);
}
