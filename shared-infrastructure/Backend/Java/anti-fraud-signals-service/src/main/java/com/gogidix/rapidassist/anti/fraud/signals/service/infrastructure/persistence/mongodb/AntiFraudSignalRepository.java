package com.gogidix.rapidassist.anti.fraud.signals.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.signals.service.domain.model.AntiFraudSignal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for AntiFraudSignal with MANDATORY tenant filtering.
 * CRITICAL: All queries MUST filter by tenantId to prevent cross-tenant data leakage.
 */
@Repository
public interface AntiFraudSignalRepository extends MongoRepository<AntiFraudSignalDocument, String> {

    /**
     * Find all signals for a specific tenant with pagination.
     * CRITICAL: Always filter by tenantId.
     */
    Page<AntiFraudSignalDocument> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Find all signals for a specific transaction within a tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndTransactionId(String tenantId, String transactionId);

    /**
     * Find unresolved signals for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndResolvedFalse(String tenantId);

    /**
     * Find signals by severity for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndSeverityOrderByCreatedAtDesc(
            String tenantId,
            AntiFraudSignal.SignalSeverity severity
    );

    /**
     * Find signals by severity range for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndRiskScoreGreaterThanEqualOrderByRiskScoreDesc(
            String tenantId,
            double minRiskScore
    );

    /**
     * Find signals created after a timestamp for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndCreatedAtAfterOrderByCreatedAtDesc(
            String tenantId,
            Instant createdAt
    );

    /**
     * Find signals created within a date range for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            String tenantId,
            Instant startDate,
            Instant endDate
    );

    /**
     * Find a specific signal by ID and tenant.
     * CRITICAL: Always filter by tenantId to prevent cross-tenant access.
     */
    Optional<AntiFraudSignalDocument> findByIdAndTenantId(String id, String tenantId);

    /**
     * Check if a signal exists for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    boolean existsByIdAndTenantId(String id, String tenantId);

    /**
     * Count unresolved signals for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    long countByTenantIdAndResolvedFalse(String tenantId);

    /**
     * Count signals by severity for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    long countByTenantIdAndSeverity(String tenantId, AntiFraudSignal.SignalSeverity severity);

    /**
     * Delete all signals for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    void deleteByTenantId(String tenantId);

    /**
     * Find high-risk signals for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    @Query("{ 'tenantId': ?0, 'riskScore': { $gte: ?1 }, 'resolved': false }")
    List<AntiFraudSignalDocument> findHighRiskUnresolvedByTenant(String tenantId, double minRiskScore);

    /**
     * Find signals by signal type for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudSignalDocument> findByTenantIdAndSignalTypeOrderByCreatedAtDesc(
            String tenantId,
            String signalType
    );

    /**
     * Find recent signals for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    @Query("{ 'tenantId': ?0, 'resolved': false }")
    List<AntiFraudSignalDocument> findUnresolvedByTenantOrderBySeverityDescCreatedAtDesc(String tenantId);
}
