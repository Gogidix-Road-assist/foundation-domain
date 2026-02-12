package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PriceHistoryEntity.
 */
@Repository
public interface SpringDataPriceHistoryRepository extends MongoRepository<PriceHistoryEntity, String> {

    /**
     * Find price history by UUID and tenant.
     */
    Optional<PriceHistoryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find price history by UUID.
     */
    Optional<PriceHistoryEntity> findByUuid(UUID uuid);

    /**
     * Find all price history by tenant.
     */
    List<PriceHistoryEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    /**
     * Find price history by product ID and tenant.
     */
    List<PriceHistoryEntity> findByProductIdAndTenantIdOrderByCreatedAtDesc(String productId, String tenantId);

    /**
     * Find price history by product ID.
     */
    List<PriceHistoryEntity> findByProductIdOrderByCreatedAtDesc(String productId);

    /**
     * Find price history by date range.
     */
    @Query("{ 'tenantId': ?0, 'productId': ?1, 'createdAt': { $gte: ?2, $lte: ?3 } }")
    List<PriceHistoryEntity> findByProductIdAndTenantIdAndDateRange(
            String tenantId, String productId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find latest price history for product.
     */
    List<PriceHistoryEntity> findTop10ByProductIdAndTenantIdOrderByCreatedAtDesc(
            String productId, String tenantId);

    /**
     * Delete price history by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count price history entries by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count price history entries by product.
     */
    long countByProductIdAndTenantId(String productId, String tenantId);
}
