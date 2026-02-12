package com.gogidix.rapidassist.ai.pricing.domain.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for PriceHistory aggregate.
 * Defines the contract for price history persistence operations.
 */
public interface PriceHistoryRepositoryPort {

    PriceHistory save(PriceHistory priceHistory);

    Optional<PriceHistory> findById(UUID id);

    Optional<PriceHistory> findByIdAndTenantId(UUID id, String tenantId);

    List<PriceHistory> findByTenantId(String tenantId);

    List<PriceHistory> findByProductId(String productId);

    List<PriceHistory> findByProductIdAndTenantId(String productId, String tenantId);

    List<PriceHistory> findByProductIdAndTenantIdAndDateRange(
            String productId, String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);
}
