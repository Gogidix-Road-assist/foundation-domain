package com.gogidix.rapidassist.ai.pricing.domain.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.CompetitivePrice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for CompetitivePrice aggregate.
 * Defines the contract for competitive pricing persistence operations.
 */
public interface CompetitivePriceRepositoryPort {

    CompetitivePrice save(CompetitivePrice competitivePrice);

    Optional<CompetitivePrice> findById(UUID id);

    Optional<CompetitivePrice> findByIdAndTenantId(UUID id, String tenantId);

    List<CompetitivePrice> findByTenantId(String tenantId);

    List<CompetitivePrice> findByProductId(String productId);

    List<CompetitivePrice> findByProductIdAndTenantId(String productId, String tenantId);

    List<CompetitivePrice> findByCompetitorNameAndTenantId(String competitorName, String tenantId);

    void deleteById(UUID id);

    void deleteByIdAndTenantId(UUID id, String tenantId);
}
