package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceElasticityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PriceElasticityEntity.
 */
@Repository
public interface SpringDataPriceElasticityRepository extends MongoRepository<PriceElasticityEntity, String> {

    /**
     * Find price elasticity by UUID and tenant.
     */
    Optional<PriceElasticityEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find price elasticity by UUID.
     */
    Optional<PriceElasticityEntity> findByUuid(UUID uuid);

    /**
     * Find all price elasticity by tenant.
     */
    List<PriceElasticityEntity> findByTenantId(String tenantId);

    /**
     * Find price elasticity by product ID and tenant.
     */
    List<PriceElasticityEntity> findByProductIdAndTenantIdOrderByCreatedAtDesc(String productId, String tenantId);

    /**
     * Find price elasticity by product ID.
     */
    List<PriceElasticityEntity> findByProductIdOrderByCreatedAtDesc(String productId);

    /**
     * Find latest price elasticity for product and tenant.
     */
    Optional<PriceElasticityEntity> findFirstByProductIdAndTenantIdOrderByCreatedAtDesc(
            String productId, String tenantId);

    /**
     * Find price elasticity by elasticity type and tenant.
     */
    List<PriceElasticityEntity> findByElasticityTypeAndTenantId(String elasticityType, String tenantId);

    /**
     * Delete price elasticity by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count price elasticity by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count price elasticity by product.
     */
    long countByProductIdAndTenantId(String productId, String tenantId);
}
