package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRule;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudRuleRepositoryPort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FraudRuleRepositoryAdapter implements FraudRuleRepositoryPort {

    private final List<FraudRule> rules = new ArrayList<>();

    @Override
    public FraudRule save(String tenantId, FraudRule rule) {
        // Remove existing rule with same ID if it exists (for updates)
        if (rule.getId() != null) {
            rules.removeIf(r -> rule.getId().equals(r.getId()));
        }
        rules.add(rule);
        return rule;
    }

    @Override
    public Optional<FraudRule> findById(String tenantId, UUID id) {
        return rules.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    @Override
    public Optional<FraudRule> findByRuleCode(String tenantId, String ruleCode) {
        return rules.stream().filter(r -> r.getTenantId().equals(tenantId) && r.getRuleCode().equals(ruleCode)).findFirst();
    }

    @Override
    public List<FraudRule> findByTenantId(String tenantId) {
        return rules.stream().filter(r -> r.getTenantId().equals(tenantId)).toList();
    }

    @Override
    public List<FraudRule> findByRuleType(String tenantId, String ruleType) {
        return rules.stream().filter(r -> r.getTenantId().equals(tenantId) && r.getRuleType().equals(ruleType)).toList();
    }

    @Override
    public List<FraudRule> findActiveRules(String tenantId) {
        return rules.stream().filter(r -> r.getTenantId().equals(tenantId) && r.isActive()).toList();
    }

    @Override
    public void delete(String tenantId, UUID id) {
        rules.removeIf(r -> r.getId().equals(id));
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return rules.stream().anyMatch(r -> r.getId().equals(id));
    }
}
