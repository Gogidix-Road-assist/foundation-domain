package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceElasticity;
import com.gogidix.rapidassist.ai.pricing.domain.repository.PriceElasticityRepositoryPort;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceElasticityEntity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper.PriceElasticityPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PriceElasticityRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PriceElasticityRepositoryImpl implements PriceElasticityRepositoryPort {

    private final SpringDataPriceElasticityRepository springDataRepository;
    private final PriceElasticityPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public PriceElasticity save(PriceElasticity priceElasticity) {
        log.info("Saving price elasticity: {} for tenant: {}", priceElasticity.getId(), priceElasticity.getTenantId());

        PriceElasticityEntity entity = persistenceMapper.toEntity(priceElasticity);
        PriceElasticityEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceElasticity> findById(UUID id) {
        log.info("Finding price elasticity by ID: {}", id);

        return springDataRepository.findByUuid(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceElasticity> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding price elasticity by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceElasticity> findByTenantId(String tenantId) {
        log.info("Finding all price elasticity for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceElasticity> findByProductId(String productId) {
        log.info("Finding price elasticity for product: {}", productId);

        return springDataRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceElasticity> findByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding price elasticity for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findByProductIdAndTenantIdOrderByCreatedAtDesc(productId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceElasticity> findLatestByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding latest price elasticity for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findFirstByProductIdAndTenantIdOrderByCreatedAtDesc(productId, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting price elasticity by ID: {}", id);

        springDataRepository.findByUuid(id).ifPresent(entity -> {
            springDataRepository.delete(entity);
        });
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting price elasticity: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }
}
