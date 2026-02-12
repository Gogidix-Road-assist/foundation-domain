package com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.optimization.infrastructure.persistence.entity.HyperparameterEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for HyperparameterEntity.
 */
@Repository
public interface SpringDataHyperparameterRepository extends MongoRepository<HyperparameterEntity, String> {

    /**
     * Find hyperparameter by UUID and tenant.
     */
    Optional<HyperparameterEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find hyperparameter by name and tenant.
     */
    Optional<HyperparameterEntity> findByNameAndTenantId(String name, String tenantId);

    /**
     * Find all hyperparameters for a tenant.
     */
    List<HyperparameterEntity> findByTenantId(String tenantId);

    /**
     * Find hyperparameters by category and tenant.
     */
    List<HyperparameterEntity> findByCategoryAndTenantId(String category, String tenantId);

    /**
     * Find hyperparameters by type and tenant.
     */
    List<HyperparameterEntity> findByTypeAndTenantId(String type, String tenantId);

    /**
     * Find hyperparameters by name pattern (case-insensitive).
     */
    List<HyperparameterEntity> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String namePattern);

    /**
     * Check if hyperparameter exists by name and tenant.
     */
    boolean existsByNameAndTenantId(String name, String tenantId);

    /**
     * Count hyperparameters by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Delete hyperparameter by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
