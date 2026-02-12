package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceHistory;
import com.gogidix.rapidassist.ai.pricing.domain.repository.PriceHistoryRepositoryPort;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceHistoryEntity;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper.PriceHistoryPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of PriceHistoryRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class PriceHistoryRepositoryImpl implements PriceHistoryRepositoryPort {

    private final SpringDataPriceHistoryRepository springDataRepository;
    private final PriceHistoryPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public PriceHistory save(PriceHistory priceHistory) {
        log.info("Saving price history: {} for tenant: {}", priceHistory.getId(), priceHistory.getTenantId());

        PriceHistoryEntity entity = persistenceMapper.toEntity(priceHistory);
        PriceHistoryEntity savedEntity = springDataRepository.save(entity);

        return persistenceMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceHistory> findById(UUID id) {
        log.info("Finding price history by ID: {}", id);

        return springDataRepository.findByUuid(id)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PriceHistory> findByIdAndTenantId(UUID id, String tenantId) {
        log.info("Finding price history by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(persistenceMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceHistory> findByTenantId(String tenantId) {
        log.info("Finding all price history for tenant: {}", tenantId);

        return springDataRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceHistory> findByProductId(String productId) {
        log.info("Finding price history for product: {}", productId);

        return springDataRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceHistory> findByProductIdAndTenantId(String productId, String tenantId) {
        log.info("Finding price history for product: {} in tenant: {}", productId, tenantId);

        return springDataRepository.findByProductIdAndTenantIdOrderByCreatedAtDesc(productId, tenantId).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PriceHistory> findByProductIdAndTenantIdAndDateRange(
            String productId, String tenantId, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Finding price history for product: {} in tenant: {} between {} and {}",
                productId, tenantId, startDate, endDate);

        return springDataRepository.findByProductIdAndTenantIdAndDateRange(
                tenantId, productId, startDate, endDate).stream()
                .map(persistenceMapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting price history by ID: {}", id);

        springDataRepository.findByUuid(id).ifPresent(entity -> {
            springDataRepository.delete(entity);
        });
    }

    @Override
    @Transactional
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        log.info("Deleting price history: {} for tenant: {}", id, tenantId);

        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }
}
