package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.impl;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import com.gogidix.rapidassist.ai.dataquality.domain.repository.DataQualityRuleRepositoryPort;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.adapter.DataQualityRepositoryAdapter;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityRuleEntity;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository.SpringDataQualityRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of DataQualityRuleRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class DataQualityRuleRepositoryImpl implements DataQualityRuleRepositoryPort {

    private final SpringDataQualityRuleRepository springRepository;

    @Override
    public DataQualityRule save(DataQualityRule rule) {
        if (rule.getCreatedAt() == null) {
            rule.setCreatedAt(LocalDateTime.now());
        }
        rule.setUpdatedAt(LocalDateTime.now());

        DataQualityRuleEntity entity = DataQualityRepositoryAdapter.toEntity(rule);
        DataQualityRuleEntity savedEntity = springRepository.save(entity);
        return DataQualityRepositoryAdapter.toDomain(savedEntity);
    }

    @Override
    public Optional<DataQualityRule> findById(String tenantId, UUID id) {
        return springRepository.findByTenantIdAndId(tenantId, id)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public Optional<DataQualityRule> findByTenantIdAndName(String tenantId, String name) {
        return springRepository.findByTenantIdAndName(tenantId, name)
                .map(DataQualityRepositoryAdapter::toDomain);
    }

    @Override
    public List<DataQualityRule> findByTenantId(String tenantId) {
        return springRepository.findByTenantId(tenantId).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityRule> findByTenantIdAndActive(String tenantId, boolean active) {
        return springRepository.findByTenantIdAndActive(tenantId, active).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityRule> findByTenantIdAndEntityType(String tenantId, String entityType) {
        return springRepository.findByTenantIdAndEntityType(tenantId, entityType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<DataQualityRule> findByTenantIdAndRuleType(String tenantId, DataQualityRule.RuleType ruleType) {
        return springRepository.findByTenantIdAndRuleType(tenantId, ruleType).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String tenantId, UUID id) {
        springRepository.findByTenantIdAndId(tenantId, id).ifPresent(entity -> {
            springRepository.delete(entity);
        });
    }

    @Override
    public List<DataQualityRule> findByTenantIdAndSeverity(String tenantId, DataQualityRule.RuleSeverity severity) {
        return springRepository.findByTenantIdAndSeverity(tenantId, severity).stream()
                .map(DataQualityRepositoryAdapter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        return springRepository.existsByTenantIdAndName(tenantId, name);
    }
}
