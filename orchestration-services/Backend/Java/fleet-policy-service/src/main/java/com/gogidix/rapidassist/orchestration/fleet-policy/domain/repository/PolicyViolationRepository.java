package com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PolicyViolation entities
 * Port for policy violation data access operations
 */
public interface PolicyViolationRepository {

    /**
     * Save violation record
     */
    PolicyViolation save(PolicyViolation violation);

    /**
     * Find violation by ID
     */
    Optional<PolicyViolation> findById(String id);

    /**
     * Find violations by policy ID
     */
    List<PolicyViolation> findByPolicyId(String policyId);

    /**
     * Find violations by rule ID
     */
    List<PolicyViolation> findByRuleId(String ruleId);

    /**
     * Find violations by entity
     */
    List<PolicyViolation> findByEntityTypeAndEntityId(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    );

    /**
     * Find violations by tenant
     */
    List<PolicyViolation> findByTenantId(String tenantId);

    /**
     * Find violations by status
     */
    List<PolicyViolation> findByTenantIdAndStatus(
            String tenantId,
            PolicyViolation.ViolationStatus status
    );

    /**
     * Find violations by severity
     */
    List<PolicyViolation> findByTenantIdAndSeverity(
            String tenantId,
            PolicyViolation.ViolationSeverity severity
    );

    /**
     * Find open violations for entity
     */
    List<PolicyViolation> findOpenViolationsByEntity(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    );

    /**
     * Find violations by date range
     */
    List<PolicyViolation> findByTenantIdAndDetectedAtBetween(
            String tenantId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    /**
     * Find overdue violations
     */
    List<PolicyViolation> findOverdueViolations(String tenantId);

    /**
     * Find violations requiring escalation
     */
    List<PolicyViolation> findViolationsRequiringEscalation(String tenantId);

    /**
     * Search violations by entity name
     */
    List<PolicyViolation> searchByEntityName(String tenantId, String entityName);

    /**
     * Delete violation by ID
     */
    void deleteById(String id);

    /**
     * Count violations by entity
     */
    long countByEntityTypeAndEntityId(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    );

    /**
     * Count violations by status
     */
    long countByTenantIdAndStatus(
            String tenantId,
            PolicyViolation.ViolationStatus status
    );

    /**
     * Calculate total violation points
     */
    Integer calculateTotalPoints(
            PolicyViolation.ViolationEntityType entityType,
            String entityId
    );
}
