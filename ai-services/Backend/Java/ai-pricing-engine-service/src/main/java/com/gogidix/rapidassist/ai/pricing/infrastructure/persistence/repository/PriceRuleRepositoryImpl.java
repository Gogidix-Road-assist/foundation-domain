package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRule;
import com.gogidix.rapidassist.ai.pricing.domain.repository.PriceRuleRepositoryPort;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceRuleEntity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper.PriceRulePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PriceRuleRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PriceRuleRepositoryImpl implements PriceRuleRepositoryPort {

    private final SpringDataPriceRuleRepository springDataRepository;
    private final PriceRulePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public PriceRule save(PriceRule priceRule) {
        log.info("Saving price rule: {} for tenant: {}", priceRule.getId(), priceRule.getTenantId());

        PriceRuleEntity entity = persistenceMapper.toEntity(priceRule);
        PriceRuleEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceRule> findById(UUID id) {
        log.info("Finding price rule by ID: {}", id);

        return springDataRepository.findByUuid(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceRule> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding price rule by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceRule> findByTenantId(String tenantId) {
        log.info("Finding all price rules for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceRule> findByProductId(String productId) {
        log.info("Finding price rules for product: {}", productId);

        return springDataRepository.findByProductId(productId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceRule> findByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding price rules for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceRule> findActiveRulesByProductAndTenantId(String productId, String tenantId) {
        log.info("Finding active price rules for product: {} in tenant: {}", productId, tenantId);

        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findActiveRulesByProductAndTenantId(tenantId, productId, now).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting price rule by ID: {}", id);

        springDataRepository.findByUuid(id).ifPresent(entity -> {
            springDataRepository.delete(entity);
        });
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting price rule: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }
}
