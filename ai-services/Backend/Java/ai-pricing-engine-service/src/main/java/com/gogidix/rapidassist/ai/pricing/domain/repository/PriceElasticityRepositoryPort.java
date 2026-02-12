package com.gogidix.rapidassist.ai.pricing.domain.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceElasticity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for PriceElasticity aggregate.
 * Defines the contract for price elasticity persistence operations.
 */
public interface PriceElasticityRepositoryPort {

    PriceElasticity save(PriceElasticity priceElasticity);

    Optional<PriceElasticity> findById(UUID id);

    Optional<PriceElasticity> findByIdAndTenantId(UUID id, String tenantId);

    List<PriceElasticity> findByTenantId(String tenantId);

    List<PriceElasticity> findByProductId(String productId);

    List<PriceElasticity> findByProductIdAndTenantId(String productId, String tenantId);

    Optional<PriceElasticity> findLatestByProductIdAndTenantId(String productId, String tenantId);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);
}
