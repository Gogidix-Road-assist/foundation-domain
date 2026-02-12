package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingRule;
import com.gogidix.rapidassist.ai.matching.domain.repository.MatchingRuleRepositoryPort;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingRuleEntity;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper.MatchingRulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of MatchingRuleRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class MatchingRuleRepositoryImpl implements MatchingRuleRepositoryPort {

    private final SpringDataMatchingRuleRepository springDataRepository;
    private final MatchingRulePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public MatchingRule save(String tenantId, MatchingRule matchingRule) {
        log.info("Saving matching rule: {} for tenant: {}", matchingRule.getId(), tenantId);

        MatchingRuleEntity entity = persistenceMapper.toEntity(matchingRule);
        MatchingRuleEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingRule> findById(String tenantId, UUID id) {
        log.info("Finding matching rule by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingRule> findByRuleCode(String tenantId, String ruleCode) {
        log.info("Finding matching rule by code: {} for tenant: {}", ruleCode, tenantId);

        return springDataRepository.findByRuleCodeAndTenantId(ruleCode, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingRule> findByTenantId(String tenantId) {
        log.info("Finding all matching rules for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingRule> findByEntityType(String tenantId, String sourceEntityType, String targetEntityType) {
        log.info("Finding matching rules for entity types: {} -> {} in tenant: {}",
                 sourceEntityType, targetEntityType, tenantId);

        return springDataRepository.findBySourceEntityTypeAndTargetEntityTypeAndTenantId(
                sourceEntityType, targetEntityType, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchingRule> findActiveRules(String tenantId) {
        log.info("Finding active matching rules for tenant: {}", tenantId);

        return springDataRepository.findByActiveTrueAndTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting matching rule: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }
}
