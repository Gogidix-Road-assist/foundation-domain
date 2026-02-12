package com.gogidix.rapidassist.ai.optimization.domain.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationAlgorithm;
import com.gogidix.rapidassist.ai.optimization.domain.model.OptimizationConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving OptimizationConfiguration entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface OptimizationConfigurationRepositoryPort {

    /**
     * Save an optimization configuration.
     */
    OptimizationConfiguration save(String tenantId, OptimizationConfiguration configuration);

    /**
     * Find a configuration by ID and tenant.
     */
    Optional<OptimizationConfiguration> findById(String tenantId, UUID configId);

    /**
     * Find configurations by algorithm.
     */
    List<OptimizationConfiguration> findByAlgorithm(String tenantId, OptimizationAlgorithm algorithm);

    /**
     * Find all configurations for a tenant.
     */
    List<OptimizationConfiguration> findByTenantId(String tenantId);

    /**
     * Find configurations by creator.
     */
    List<OptimizationConfiguration> findByCreatedBy(String tenantId, String createdBy);

    /**
     * Delete a configuration by ID.
     */
    void delete(String tenantId, UUID configId);

    /**
     * Check if a configuration exists.
     */
    boolean exists(String tenantId, UUID configId);

    /**
     * Count configurations by tenant.
     */
    long countByTenantId(String tenantId);
}
