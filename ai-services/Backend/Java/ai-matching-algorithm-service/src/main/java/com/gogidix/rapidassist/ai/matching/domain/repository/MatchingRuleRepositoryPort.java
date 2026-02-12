package com.gogidix.rapidassist.ai.matching.domain.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for MatchingRule operations.
 * Defines the contract for matching rule persistence.
 */
public interface MatchingRuleRepositoryPort {

    MatchingRule save(String tenantId, MatchingRule matchingRule);

    Optional<MatchingRule> findById(String tenantId, UUID id);

    Optional<MatchingRule> findByRuleCode(String tenantId, String ruleCode);

    List<MatchingRule> findByTenantId(String tenantId);

    List<MatchingRule> findByEntityType(String tenantId, String sourceEntityType, String targetEntityType);

    List<MatchingRule> findActiveRules(String tenantId);

    void delete(String tenantId, UUID id);

    boolean exists(String tenantId, UUID id);

    long countByTenantId(String tenantId);
}
