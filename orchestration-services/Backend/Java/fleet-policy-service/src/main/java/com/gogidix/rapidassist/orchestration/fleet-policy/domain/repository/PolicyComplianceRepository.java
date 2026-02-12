package com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyCompliance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PolicyCompliance entities
 * Port for policy compliance data access operations
 */
public interface PolicyComplianceRepository {

    /**
     * Save compliance record
     */
    PolicyCompliance save(PolicyCompliance compliance);

    /**
     * Find compliance by ID
     */
    Optional<PolicyCompliance> findById(String id);

    /**
     * Find compliance by policy, entity type, and entity ID
     */
    Optional<PolicyCompliance> findByPolicyIdAndEntityTypeAndEntityId(
            String policyId,
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    );

    /**
     * Find compliance by policy and date
     */
    List<PolicyCompliance> findByPolicyIdAndComplianceDate(
            String policyId,
            LocalDate complianceDate
    );

    /**
     * Find all compliance records for an entity
     */
    List<PolicyCompliance> findByEntityTypeAndEntityId(
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    );

    /**
     * Find compliance records by tenant
     */
    List<PolicyCompliance> findByTenantId(String tenantId);

    /**
     * Find compliance by status
     */
    List<PolicyCompliance> findByTenantIdAndStatus(
            String tenantId,
            PolicyCompliance.ComplianceStatus status
    );

    /**
     * Find compliance requiring action
     */
    List<PolicyCompliance> findByTenantIdAndRequiresAction(
            String tenantId,
            Boolean requiresAction
    );

    /**
     * Find overdue compliance actions
     */
    List<PolicyCompliance> findOverdueActions(String tenantId);

    /**
     * Find compliance for date range
     */
    List<PolicyCompliance> findByTenantIdAndComplianceDateBetween(
            String tenantId,
            LocalDate startDate,
            LocalDate endDate
    );

    /**
     * Delete compliance by ID
     */
    void deleteById(String id);

    /**
     * Calculate average compliance score for entity
     */
    Double calculateAverageComplianceScore(
            PolicyCompliance.ComplianceEntityType entityType,
            String entityId
    );

    /**
     * Count non-compliant records
     */
    long countByTenantIdAndStatus(
            String tenantId,
            PolicyCompliance.ComplianceStatus status
    );
}
