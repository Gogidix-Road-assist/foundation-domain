package com.gogidix.rapidassist.ai.optimization.domain.repository;

import com.gogidix.rapidassist.ai.optimization.domain.model.Hyperparameter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving Hyperparameter entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface HyperparameterRepositoryPort {

    /**
     * Save a hyperparameter.
     */
    Hyperparameter save(String tenantId, Hyperparameter hyperparameter);

    /**
     * Find a hyperparameter by ID and tenant.
     */
    Optional<Hyperparameter> findById(String tenantId, UUID hyperparameterId);

    /**
     * Find a hyperparameter by name and tenant.
     */
    Optional<Hyperparameter> findByName(String tenantId, String name);

    /**
     * Find all hyperparameters for a tenant.
     */
    List<Hyperparameter> findByTenantId(String tenantId);

    /**
     * Find hyperparameters by category.
     */
    List<Hyperparameter> findByCategory(String tenantId, String category);

    /**
     * Find hyperparameters by type.
     */
    List<Hyperparameter> findByType(String tenantId, String type);

    /**
     * Find hyperparameters by name pattern.
     */
    List<Hyperparameter> findByNameContaining(String tenantId, String namePattern);

    /**
     * Delete a hyperparameter by ID.
     */
    void delete(String tenantId, UUID hyperparameterId);

    /**
     * Check if a hyperparameter exists by name.
     */
    boolean existsByName(String tenantId, String name);

    /**
     * Count hyperparameters by tenant.
     */
    long countByTenantId(String tenantId);
}
