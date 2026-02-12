package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.Discount;
import com.gogidix.rapidassist.ai.pricing.domain.repository.DiscountRepositoryPort;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.DiscountEntity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper.DiscountPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of DiscountRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class DiscountRepositoryImpl implements DiscountRepositoryPort {

    private final SpringDataDiscountRepository springDataRepository;
    private final DiscountPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public Discount save(Discount discount) {
        log.info("Saving discount: {} for tenant: {}", discount.getId(), discount.getTenantId());

        DiscountEntity entity = persistenceMapper.toEntity(discount);
        DiscountEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Discount> findById(UUID id) {
        log.info("Finding discount by ID: {}", id);

        return springDataRepository.findByUuid(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Discount> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding discount by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Discount> findByCodeAndTenantId(String code, String tenantId) {
        log.info("Finding discount by code: {} for tenant: {}", code, tenantId);

        return springDataRepository.findByCodeAndTenantId(code, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findByTenantId(String tenantId) {
        log.info("Finding all discounts for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding discounts for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findByProductIdAndTenantId(productId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Discount> findActiveDiscountsByTenantId(String tenantId) {
        log.info("Finding active discounts for tenant: {}", tenantId);

        LocalDateTime now = LocalDateTime.now();
        return springDataRepository.findActiveDiscountsByTenantId(tenantId, now).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting discount by ID: {}", id);

        springDataRepository.findByUuid(id).ifPresent(entity -> {
            springDataRepository.delete(entity);
        });
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting discount: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }
}
