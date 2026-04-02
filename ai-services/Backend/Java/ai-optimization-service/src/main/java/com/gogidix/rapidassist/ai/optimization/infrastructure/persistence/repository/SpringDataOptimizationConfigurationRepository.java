package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.OptimizationConfigurationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for OptimizationConfigurationEntity.
 */
@Repository
public interface SpringDataOptimizationConfigurationRepository extends MongoRepository<OptimizationConfigurationEntity, String> {

    /**
     * Find configuration by UUID and tenant.
     */
    Optional<OptimizationConfigurationEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find configurations by algorithm and tenant.
     */
    List<OptimizationConfigurationEntity> findByAlgorithmAndTenantId(OptimizationAlgorithm algorithm, String tenantId);

    /**
     * Find all configurations for a tenant.
     */
    List<OptimizationConfigurationEntity> findByTenantId(String tenantId);

    /**
     * Find configurations by creator and tenant.
     */
    List<OptimizationConfigurationEntity> findByCreatedByAndTenantId(String createdBy, String tenantId);

    /**
     * Check if configuration exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count configurations by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Delete configuration by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
