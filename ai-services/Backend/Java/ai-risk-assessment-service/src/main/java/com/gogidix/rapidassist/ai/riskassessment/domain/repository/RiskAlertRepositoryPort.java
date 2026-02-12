package com.gogidix.rapidassist.ai.riskassessment.domain.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.AlertPriority;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Risk Alert
 * Defines the contract for risk alert persistence operations
 */
public interface RiskAlertRepositoryPort {

    /**
     * Save risk alert
     */
    RiskAlert save(RiskAlert alert);

    /**
     * Find risk alert by ID and tenant
     */
    Optional<RiskAlert> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all alerts by risk assessment and tenant
     */
    List<RiskAlert> findByRiskAssessmentIdAndTenantId(UUID riskAssessmentId, String tenantId);

    /**
     * Find alerts by status and tenant
     */
    List<RiskAlert> findByStatusAndTenantId(AlertStatus status, String tenantId);

    /**
     * Find alerts by priority and tenant
     */
    List<RiskAlert> findByPriorityAndTenantId(AlertPriority priority, String tenantId);

    /**
     * Find alerts by category and tenant
     */
    List<RiskAlert> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find alerts assigned to user and tenant
     */
    List<RiskAlert> findByAssignedToAndTenantId(String assignedTo, String tenantId);

    /**
     * Find active alerts for tenant
     */
    List<RiskAlert> findActiveAlerts(String tenantId);

    /**
     * Find unresolved alerts for tenant
     */
    List<RiskAlert> findUnresolvedAlerts(String tenantId);

    /**
     * Find overdue alerts for tenant
     */
    List<RiskAlert> findOverdueAlerts(LocalDateTime threshold, String tenantId);

    /**
     * Find alerts created within date range
     */
    List<RiskAlert> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find critical alerts for tenant
     */
    List<RiskAlert> findCriticalAlerts(String tenantId);

    /**
     * Check if alert exists
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Delete alert by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count alerts by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count alerts by status and tenant
     */
    long countByStatusAndTenantId(AlertStatus status, String tenantId);
}
