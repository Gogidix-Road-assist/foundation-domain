package com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyRule;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PolicyRule entities
 * Port for policy rule data access operations
 */
public interface PolicyRuleRepository {

    /**
     * Save a policy rule
     */
    PolicyRule save(PolicyRule rule);

    /**
     * Find rule by ID
     */
    Optional<PolicyRule> findById(String id);

    /**
     * Find rule by tenant ID and rule code
     */
    Optional<PolicyRule> findByTenantIdAndRuleCode(String tenantId, String ruleCode);

    /**
     * Find all rules for a policy
     */
    List<PolicyRule> findByPolicyId(String policyId);

    /**
     * Find all rules for a tenant
     */
    List<PolicyRule> findByTenantId(String tenantId);

    /**
     * Find active rules for a tenant
     */
    List<PolicyRule> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find rules by type
     */
    List<PolicyRule> findByTenantIdAndRuleType(String tenantId, PolicyRule.RuleType ruleType);

    /**
     * Find mandatory rules for a policy
     */
    List<PolicyRule> findByPolicyIdAndIsMandatory(String policyId, Boolean isMandatory);

    /**
     * Search rules by name or description
     */
    List<PolicyRule> searchRules(String tenantId, String searchTerm);

    /**
     * Delete rule by ID
     */
    void deleteById(String id);

    /**
     * Delete all rules for a policy
     */
    void deleteByPolicyId(String policyId);

    /**
     * Check if rule code exists
     */
    boolean existsByTenantIdAndRuleCode(String tenantId, String ruleCode);

    /**
     * Count rules by policy
     */
    long countByPolicyId(String policyId);
}
