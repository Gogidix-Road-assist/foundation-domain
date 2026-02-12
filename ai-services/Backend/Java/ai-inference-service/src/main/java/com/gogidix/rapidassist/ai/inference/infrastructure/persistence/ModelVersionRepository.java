package com.gogidix.rapidassist.ai.inference.infrastructure.persistence;

import com.gogidix.rapidassist.ai.inference.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.inference.application.port.out.ModelVersionRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * MongoDB implementation of ModelVersionRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ModelVersionRepository implements ModelVersionRepositoryPort {

    private final ModelVersionMongoRepository mongoRepository;

    @Override
    public ModelVersion save(ModelVersion modelVersion) {
        log.debug("Saving model version: {} - {}", modelVersion.getModelId(), modelVersion.getVersion());
        return mongoRepository.save(modelVersion);
    }

    @Override
    public Optional<ModelVersion> findById(UUID id) {
        log.debug("Finding model version by id: {}", id);
        return mongoRepository.findById(id);
    }

    @Override
    public Optional<ModelVersion> findByModelIdAndVersion(String modelId, String version) {
        log.debug("Finding model version: {} - {}", modelId, version);
        return mongoRepository.findByModelIdAndVersion(modelId, version);
    }

    @Override
    public List<ModelVersion> findByModelId(String modelId) {
        log.debug("Finding versions for model: {}", modelId);
        return mongoRepository.findByModelId(modelId);
    }

    @Override
    public List<ModelVersion> findByTenantId(String tenantId) {
        log.debug("Finding model versions for tenant: {}", tenantId);
        return mongoRepository.findByTenantId(tenantId);
    }

    @Override
    public List<ModelVersion> findByStatus(com.gogidix.rapidassist.ai.inference.domain.model.ModelVersionStatus status) {
        log.debug("Finding model versions by status: {}", status);
        return mongoRepository.findByStatus(status);
    }

    @Override
    public Optional<ModelVersion> findDefaultVersionByModelId(String modelId) {
        log.debug("Finding default version for model: {}", modelId);
        return mongoRepository.findByModelIdAndIsTrue(modelId);
    }

    @Override
    public void deleteById(UUID id) {
        log.debug("Deleting model version: {}", id);
        mongoRepository.deleteById(id);
    }

    @Override
    public List<ModelVersion> findAll() {
        log.debug("Finding all model versions");
        return mongoRepository.findAll();
    }

    @Override
    public boolean existsByModelIdAndVersion(String modelId, String version) {
        return mongoRepository.existsByModelIdAndVersion(modelId, version);
    }
}
