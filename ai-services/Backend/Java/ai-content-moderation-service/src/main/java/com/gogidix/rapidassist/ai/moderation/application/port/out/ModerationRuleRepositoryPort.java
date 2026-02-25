package com.gogidix.rapidassist.ai.moderation.application.port.out;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for ModerationRule aggregate.
 * Defines the contract for rule persistence operations.
 */
public interface ModerationRuleRepositoryPort {

    /**
     * Save a moderation rule
     */
    ModerationRule save(ModerationRule rule);

    /**
     * Find rule by ID
     */
    Optional<ModerationRule> findById(String id);

    /**
     * Find all active rules for a tenant
     */
    List<ModerationRule> findActiveByTenantId(String tenantId);

    /**
     * Find all rules for a tenant
     */
    List<ModerationRule> findByTenantId(String tenantId);

    /**
     * Find rules by type
     */
    List<ModerationRule> findByTenantIdAndType(String tenantId, ModerationRule.RuleType type);

    /**
     * Delete rule by ID
     */
    void deleteById(String id);

    /**
     * Check if rule exists by name for tenant
     */
    boolean existsByTenantIdAndName(String tenantId, String name);
}
