package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.DeploymentStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelDeployment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ModelDeployment domain operations.
 */
public interface ModelDeploymentRepositoryPort {

    /**
     * Save a model deployment.
     */
    ModelDeployment save(String tenantId, ModelDeployment deployment);

    /**
     * Find deployment by ID.
     */
    Optional<ModelDeployment> findById(String tenantId, UUID id);

    /**
     * Find all deployments for a model.
     */
    List<ModelDeployment> findByModelId(String tenantId, UUID modelId);

    /**
     * Find deployments by environment.
     */
    List<ModelDeployment> findByEnvironment(String tenantId, String environment);

    /**
     * Find deployments by status.
     */
    List<ModelDeployment> findByStatus(String tenantId, DeploymentStatus status);

    /**
     * Find active deployments by environment.
     */
    List<ModelDeployment> findActiveDeployments(String tenantId, String environment);

    /**
     * Find deployment by model version.
     */
    Optional<ModelDeployment> findByModelVersionId(String tenantId, UUID modelVersionId);

    /**
     * Check if deployment exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete a deployment.
     */
    void delete(String tenantId, UUID id);

    /**
     * Delete all deployments for a model.
     */
    void deleteByModelId(String tenantId, UUID modelId);
}
