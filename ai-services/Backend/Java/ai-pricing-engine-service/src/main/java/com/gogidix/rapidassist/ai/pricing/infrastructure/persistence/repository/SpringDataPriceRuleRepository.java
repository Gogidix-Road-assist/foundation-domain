package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRuleStatus;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PriceRuleEntity.
 */
@Repository
public interface SpringDataPriceRuleRepository extends MongoRepository<PriceRuleEntity, String> {

    /**
     * Find rule by UUID and tenant.
     */
    Optional<PriceRuleEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find rule by UUID.
     */
    Optional<PriceRuleEntity> findByUuid(UUID uuid);

    /**
     * Find all rules by tenant.
     */
    List<PriceRuleEntity> findByTenantId(String tenantId);

    /**
     * Find rules by product ID and tenant.
     */
    List<PriceRuleEntity> findByProductIdAndTenantId(String productId, String tenantId);

    /**
     * Find rules by product ID.
     */
    List<PriceRuleEntity> findByProductId(String productId);

    /**
     * Find active rules by product and tenant.
     */
    @Query("{ 'tenantId': ?0, 'productId': ?1, 'status': 'ACTIVE', " +
           "'validFrom': { $lte: ?2 }, 'validUntil': { $gte: ?2 } }")
    List<PriceRuleEntity> findActiveRulesByProductAndTenantId(String tenantId, String productId, LocalDateTime now);

    /**
     * Find active rules for tenant.
     */
    List<PriceRuleEntity> findByStatusAndTenantId(PriceRuleStatus status, String tenantId);

    /**
     * Check if rule exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete rule by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count rules by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find rules by strategy type and tenant.
     */
    List<PriceRuleEntity> findByStrategyTypeAndTenantId(
            com.gogidix.rapidassist.ai.pricing.domain.model.PricingStrategyType strategyType, String tenantId);

    /**
     * Find expiring rules.
     */
    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE', 'validUntil': { $lte: ?1 } }")
    List<PriceRuleEntity> findExpiringRules(String tenantId, LocalDateTime threshold);
}
