package com.gogidix.rapidassist.ai.inference.application.port.out;

import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for Model Version repository operations
 */
public interface ModelVersionRepositoryPort {

    ModelVersion save(ModelVersion modelVersion);

    Optional<ModelVersion> findById(UUID id);

    Optional<ModelVersion> findByModelIdAndVersion(String modelId, String version);

    List<ModelVersion> findByModelId(String modelId);

    List<ModelVersion> findByTenantId(String tenantId);

    List<ModelVersion> findByStatus(ModelVersionStatus status);

    Optional<ModelVersion> findDefaultVersionByModelId(String modelId);

    void deleteById(UUID id);

    List<ModelVersion> findAll();

    boolean existsByModelIdAndVersion(String modelId, String version);
}
