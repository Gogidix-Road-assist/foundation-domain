package com.gogidix.rapidassist.anti.fraud.rules.service.domain.port.in;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing anti-fraud rules with tenant isolation.
 * CRITICAL: All operations MUST be scoped to a specific tenant to prevent data leakage.
 */
public interface AntiFraudRuleService {

    /**
     * Create a new rule for a specific tenant.
     * CRITICAL: tenantId is mandatory and will be enforced.
     */
    AntiFraudRule createRule(String tenantId, CreateRuleRequest request);

    /**
     * Update an existing rule for a specific tenant.
     * CRITICAL: Will verify the rule belongs to the specified tenant before updating.
     */
    AntiFraudRule updateRule(String tenantId, String ruleId, UpdateRuleRequest request);

    /**
     * Find a rule by ID for a specific tenant.
     * CRITICAL: Will only return the rule if it belongs to the specified tenant.
     */
    Optional<AntiFraudRule> findById(String tenantId, String ruleId);

    /**
     * Find all rules for a specific tenant.
     * CRITICAL: Only returns rules belonging to the specified tenant.
     */
    List<AntiFraudRule> findByTenant(String tenantId);

    /**
     * Find all active rules for a specific tenant.
     * CRITICAL: Only returns active rules belonging to the specified tenant.
     */
    List<AntiFraudRule> findActiveByTenant(String tenantId);

    /**
     * Find active rules by type for a specific tenant.
     * CRITICAL: Only returns rules of the specified type belonging to the tenant.
     */
    List<AntiFraudRule> findActiveByTenantAndType(String tenantId, AntiFraudRule.RuleType ruleType);

    /**
     * Find active rules ordered by priority for a specific tenant.
     * CRITICAL: Only returns rules belonging to the specified tenant.
     */
    List<AntiFraudRule> findActiveByTenantOrderByPriority(String tenantId);

    /**
     * Delete a rule for a specific tenant.
     * CRITICAL: Will only delete the rule if it belongs to the specified tenant.
     */
    void deleteRule(String tenantId, String ruleId);

    /**
     * Activate or deactivate a rule for a specific tenant.
     * CRITICAL: Will only update the rule if it belongs to the specified tenant.
     */
    AntiFraudRule setActive(String tenantId, String ruleId, boolean active);

    /**
     * Count active rules for a specific tenant.
     */
    long countActiveByTenant(String tenantId);

    /**
     * Request DTO for creating a rule.
     */
    record CreateRuleRequest(
            String name,
            String description,
            AntiFraudRule.RuleType ruleType,
            boolean active,
            int priority,
            java.util.Map<String, Object> conditions,
            java.util.Map<String, Object> actions,
            String createdBy
    ) {}

    /**
     * Request DTO for updating a rule.
     */
    record UpdateRuleRequest(
            String name,
            String description,
            AntiFraudRule.RuleType ruleType,
            boolean active,
            int priority,
            java.util.Map<String, Object> conditions,
            java.util.Map<String, Object> actions,
            String updatedBy
    ) {}
}
