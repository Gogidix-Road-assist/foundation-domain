package com.gogidix.rapidassist.ai.dataquality.domain.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for DataQualityRule aggregate
 */
public interface DataQualityRuleRepositoryPort {

    DataQualityRule save(DataQualityRule rule);

    Optional<DataQualityRule> findById(String tenantId, UUID id);

    Optional<DataQualityRule> findByTenantIdAndName(String tenantId, String name);

    List<DataQualityRule> findByTenantId(String tenantId);

    List<DataQualityRule> findByTenantIdAndActive(String tenantId, boolean active);

    List<DataQualityRule> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityRule> findByTenantIdAndRuleType(String tenantId, DataQualityRule.RuleType ruleType);

    void deleteById(String tenantId, UUID id);

    List<DataQualityRule> findByTenantIdAndSeverity(String tenantId, DataQualityRule.RuleSeverity severity);

    boolean existsByTenantIdAndName(String tenantId, String name);
}
