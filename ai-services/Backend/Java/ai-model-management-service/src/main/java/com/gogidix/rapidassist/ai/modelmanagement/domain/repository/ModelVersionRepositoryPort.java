package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelVersion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ModelVersion domain operations.
 */
public interface ModelVersionRepositoryPort {

    /**
     * Save a model version.
     */
    ModelVersion save(String tenantId, ModelVersion modelVersion);

    /**
     * Find model version by ID.
     */
    Optional<ModelVersion> findById(String tenantId, UUID id);

    /**
     * Find all versions for a model.
     */
    List<ModelVersion> findByModelId(String tenantId, UUID modelId);

    /**
     * Find model version by version number.
     */
    Optional<ModelVersion> findByModelIdAndVersionNumber(String tenantId, UUID modelId, String versionNumber);

    /**
     * Find production-ready versions.
     */
    List<ModelVersion> findProductionReadyVersions(String tenantId, UUID modelId);

    /**
     * Find latest version.
     */
    Optional<ModelVersion> findLatestVersion(String tenantId, UUID modelId);

    /**
     * Check if version exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete a model version.
     */
    void delete(String tenantId, UUID id);

    /**
     * Delete all versions for a model.
     */
    void deleteByModelId(String tenantId, UUID modelId);
}
