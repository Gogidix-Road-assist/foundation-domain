package com.gogidix.rapidassist.ai.pricing.domain.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for PriceRule aggregate.
 * Defines the contract for pricing persistence operations.
 */
public interface PriceRuleRepositoryPort {

    PriceRule save(PriceRule priceRule);

    Optional<PriceRule> findById(UUID id);

    Optional<PriceRule> findByIdAndTenantId(UUID id, String tenantId);

    List<PriceRule> findByTenantId(String tenantId);

    List<PriceRule> findByProductId(String productId);

    List<PriceRule> findByProductIdAndTenantId(String productId, String tenantId);

    List<PriceRule> findActiveRulesByProductAndTenantId(String productId, String tenantId);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);

    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
