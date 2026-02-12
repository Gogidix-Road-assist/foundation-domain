package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.CompetitivePrice;
import com.gogidix.rapidassist.ai.pricing.domain.repository.CompetitivePriceRepositoryPort;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.CompetitivePriceEntity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper.CompetitivePricePersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of CompetitivePriceRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class CompetitivePriceRepositoryImpl implements CompetitivePriceRepositoryPort {

    private final SpringDataCompetitivePriceRepository springDataRepository;
    private final CompetitivePricePersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public CompetitivePrice save(CompetitivePrice competitivePrice) {
        log.info("Saving competitive price: {} for tenant: {}", competitivePrice.getId(), competitivePrice.getTenantId());

        CompetitivePriceEntity entity = persistenceMapper.toEntity(competitivePrice);
        CompetitivePriceEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompetitivePrice> findById(UUID id) {
        log.info("Finding competitive price by ID: {}", id);

        return springDataRepository.findByUuid(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompetitivePrice> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding competitive price by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitivePrice> findByTenantId(String tenantId) {
        log.info("Finding all competitive prices for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitivePrice> findByProductId(String productId) {
        log.info("Finding competitive prices for product: {}", productId);

        return springDataRepository.findByProductId(productId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitivePrice> findByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding competitive prices for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitivePrice> findByCompetitorNameAndTenantId(String competitorName, String tenantId) {
        log.info("Finding competitive prices for competitor: {} in tenant: {}", competitorName, tenantId);

        return springDataRepository.findByCompetitorNameAndTenantId(competitorName, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting competitive price by ID: {}", id);

        springDataRepository.findByUuid(id).ifPresent(entity -> {
            springDataRepository.delete(entity);
        });
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting competitive price: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }
}
