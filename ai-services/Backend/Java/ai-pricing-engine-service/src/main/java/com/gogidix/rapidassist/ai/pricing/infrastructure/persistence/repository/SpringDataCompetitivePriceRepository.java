package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.CompetitivePriceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for CompetitivePriceEntity.
 */
@Repository
public interface SpringDataCompetitivePriceRepository extends MongoRepository<CompetitivePriceEntity, String> {

    /**
     * Find competitive price by UUID and tenant.
     */
    Optional<CompetitivePriceEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find competitive price by UUID.
     */
    Optional<CompetitivePriceEntity> findByUuid(UUID uuid);

    /**
     * Find all competitive prices by tenant.
     */
    List<CompetitivePriceEntity> findByTenantId(String tenantId);

    /**
     * Find competitive prices by product ID and tenant.
     */
    List<CompetitivePriceEntity> findByProductIdAndTenantId(String productId, String tenantId);

    /**
     * Find competitive prices by product ID.
     */
    List<CompetitivePriceEntity> findByProductId(String productId);

    /**
     * Find competitive prices by competitor name and tenant.
     */
    List<CompetitivePriceEntity> findByCompetitorNameAndTenantId(String competitorName, String tenantId);

    /**
     * Find competitive prices by product, competitor and tenant.
     */
    Optional<CompetitivePriceEntity> findByProductIdAndCompetitorNameAndTenantId(
            String productId, String competitorName, String tenantId);

    /**
     * Find stale competitive prices (last checked before threshold).
     */
    List<CompetitivePriceEntity> findByTenantIdAndLastCheckedBefore(
            String tenantId, LocalDateTime threshold);

    /**
     * Delete competitive price by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count competitive prices by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Delete all competitive prices for a product.
     */
    void deleteByProductIdAndTenantId(String productId, String tenantId);
}
