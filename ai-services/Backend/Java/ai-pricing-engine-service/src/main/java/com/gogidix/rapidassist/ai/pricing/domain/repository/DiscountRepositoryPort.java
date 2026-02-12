package com.gogidix.rapidassist.ai.pricing.domain.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.Discount;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Discount aggregate.
 * Defines the contract for discount persistence operations.
 */
public interface DiscountRepositoryPort {

    Discount save(Discount discount);

    Optional<Discount> findById(UUID id);

    Optional<Discount> findByIdAndTenantId(UUID id, String tenantId);

    Optional<Discount> findByCodeAndTenantId(String code, String tenantId);

    List<Discount> findByTenantId(String tenantId);

    List<Discount> findByProductIdAndTenantId(String productId, String tenantId);

    List<Discount> findActiveDiscountsByTenantId(String tenantId);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);

    boolean existsByIdAndTenantId(UUID id, String tenantId);
}
