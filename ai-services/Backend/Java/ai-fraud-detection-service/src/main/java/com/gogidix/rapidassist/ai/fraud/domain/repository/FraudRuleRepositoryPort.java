package com.gogidix.rapidassist.ai.fraud.domain.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for FraudRule aggregate
 */
public interface FraudRuleRepositoryPort {

    FraudRule save(String tenantId, FraudRule rule);
    Optional<FraudRule> findById(String tenantId, UUID id);
    Optional<FraudRule> findByRuleCode(String tenantId, String ruleCode);
    List<FraudRule> findByTenantId(String tenantId);
    List<FraudRule> findByRuleType(String tenantId, String ruleType);
    List<FraudRule> findActiveRules(String tenantId);
    void delete(String tenantId, UUID id);
    boolean exists(String tenantId, UUID id);
}
