package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAlertEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RiskAlertEntity.
 */
@Repository
public interface SpringDataRiskAlertRepository extends MongoRepository<RiskAlertEntity, String> {

    /**
     * Find alert by UUID and tenant.
     */
    Optional<RiskAlertEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all alerts by risk assessment and tenant.
     */
    List<RiskAlertEntity> findByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId);

    /**
     * Find alerts by status and tenant.
     */
    List<RiskAlertEntity> findByStatusAndTenantId(AlertStatus status, String tenantId);

    /**
     * Find alerts by priority and tenant.
     */
    List<RiskAlertEntity> findByPriorityAndTenantId(AlertPriority priority, String tenantId);

    /**
     * Find alerts by category and tenant.
     */
    List<RiskAlertEntity> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find alerts assigned to user and tenant.
     */
    List<RiskAlertEntity> findByAssignedToAndTenantId(String assignedTo, String tenantId);

    /**
     * Find active alerts for tenant.
     */
    List<RiskAlertEntity> findByTenantIdAndStatusIn(String tenantId, List<AlertStatus> statuses);

    /**
     * Find unresolved alerts (not RESOLVED or DISMISSED) for tenant.
     */
    @Query("{ 'tenantId': ?0, 'status': { $nin: ['RESOLVED', 'DISMISSED'] } }")
    List<RiskAlertEntity> findUnresolvedAlerts(String tenantId);

    /**
     * Find overdue alerts (created before threshold and not resolved) for tenant.
     */
    @Query("{ 'tenantId': ?0, 'createdAt': { $lt: ?1 }, 'status': { $nin: ['RESOLVED', 'DISMISSED'] } }")
    List<RiskAlertEntity> findOverdueAlerts(String tenantId, LocalDateTime threshold);

    /**
     * Find alerts created within date range for tenant.
     */
    List<RiskAlertEntity> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find critical alerts (CRITICAL priority) for tenant.
     */
    List<RiskAlertEntity> findByTenantIdAndPriority(String tenantId, AlertPriority priority);

    /**
     * Check if alert exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete alert by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count alerts by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count alerts by status and tenant.
     */
    long countByStatusAndTenantId(AlertStatus status, String tenantId);
}
