package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.DiscountEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for DiscountEntity.
 */
@Repository
public interface SpringDataDiscountRepository extends MongoRepository<DiscountEntity, String> {

    /**
     * Find discount by UUID and tenant.
     */
    Optional<DiscountEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find discount by UUID.
     */
    Optional<DiscountEntity> findByUuid(UUID uuid);

    /**
     * Find discount by code and tenant.
     */
    Optional<DiscountEntity> findByCodeAndTenantId(String code, String tenantId);

    /**
     * Find all discounts by tenant.
     */
    List<DiscountEntity> findByTenantId(String tenantId);

    /**
     * Find discounts by product ID and tenant.
     */
    List<DiscountEntity> findByProductIdAndTenantId(String productId, String tenantId);

    /**
     * Find active discounts for tenant.
     */
    @Query("{ 'tenantId': ?0, 'active': true, " +
           "'validFrom': { $lte: ?1 }, 'validUntil': { $gte: ?1 } }")
    List<DiscountEntity> findActiveDiscountsByTenantId(String tenantId, LocalDateTime now);

    /**
     * Find discounts by customer and tenant.
     */
    List<DiscountEntity> findByCustomerIdAndTenantId(String customerId, String tenantId);

    /**
     * Check if discount exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if discount code exists.
     */
    boolean existsByCodeAndTenantId(String code, String tenantId);

    /**
     * Delete discount by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count discounts by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find discounts by discount type and tenant.
     */
    List<DiscountEntity> findByDiscountTypeAndTenantId(
            com.gogidix.rapidassist.ai.pricing.domain.model.DiscountType discountType, String tenantId);
}
