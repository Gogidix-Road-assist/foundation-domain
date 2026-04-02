package com.gogidix.rapidassist.ai.tagging.domain.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TaggingRule aggregate.
 * Defines the contract for TaggingRule persistence operations.
 */
public interface TaggingRuleRepositoryPort {

    /**
     * Save a tagging rule
     */
    TaggingRule save(TaggingRule rule);

    /**
     * Find rule by ID
     */
    Optional<TaggingRule> findById(UUID id);

    /**
     * Find rule by tenant ID and rule ID
     */
    Optional<TaggingRule> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all rules for a tenant
     */
    List<TaggingRule> findByTenantId(String tenantId);

    /**
     * Find active rules for a tenant
     */
    List<TaggingRule> findByTenantIdAndStatus(String tenantId, TaggingRule.RuleStatus status);

    /**
     * Find active rules by rule type
     */
    List<TaggingRule> findByTenantIdAndStatusAndRuleType(String tenantId, TaggingRule.RuleStatus status, TaggingRule.RuleType ruleType);

    /**
     * Find rules by priority
     */
    List<TaggingRule> findByTenantIdAndPriority(String tenantId, TaggingRule.RulePriority priority);

    /**
     * Find rules by name (case-insensitive search)
     */
    List<TaggingRule> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);

    /**
     * Check if rule name exists for tenant
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * Delete rule by ID
     */
    void deleteById(UUID id);

    /**
     * Delete rule by tenant ID and rule ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Count rules by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count active rules by tenant
     */
    long countByTenantIdAndStatus(String tenantId, TaggingRule.RuleStatus status);
}
