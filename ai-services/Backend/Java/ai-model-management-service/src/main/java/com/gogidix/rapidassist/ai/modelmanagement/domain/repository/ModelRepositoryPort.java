package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.Model;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Model domain operations.
 */
public interface ModelRepositoryPort {

    /**
     * Save a model.
     */
    Model save(String tenantId, Model model);

    /**
     * Find model by ID.
     */
    Optional<Model> findById(String tenantId, UUID id);

    /**
     * Find all models for a tenant.
     */
    List<Model> findAll(String tenantId);

    /**
     * Find models by status.
     */
    List<Model> findByStatus(String tenantId, ModelStatus status);

    /**
     * Find models by type.
     */
    List<Model> findByModelType(String tenantId, ModelType modelType);

    /**
     * Find model by name.
     */
    Optional<Model> findByName(String tenantId, String name);

    /**
     * Check if model exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete a model.
     */
    void delete(String tenantId, UUID id);
}
