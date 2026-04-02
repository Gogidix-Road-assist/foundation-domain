package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelVersion;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.ModelVersionRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelVersionEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataModelVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing ModelVersionRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ModelVersionRepositoryAdapter implements ModelVersionRepositoryPort {

    private final SpringDataModelVersionRepository springDataRepository;

    @Override
    public ModelVersion save(String tenantId, ModelVersion modelVersion) {
        ModelVersionEntity entity = toEntity(modelVersion);
        entity.setTenantId(tenantId);
        ModelVersionEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ModelVersion> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ModelVersion> findByModelId(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantIdOrderByCreatedAtDesc(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ModelVersion> findByModelIdAndVersionNumber(String tenantId, UUID modelId, String versionNumber) {
        return springDataRepository.findByModelIdAndVersionNumberAndTenantId(modelId, versionNumber, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ModelVersion> findProductionReadyVersions(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantIdAndIsProductionReadyTrue(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ModelVersion> findLatestVersion(String tenantId, UUID modelId) {
        return springDataRepository.findFirstByModelIdAndTenantIdOrderByCreatedAtDesc(modelId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void delete(String tenantId, UUID id) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteByModelId(String tenantId, UUID modelId) {
        springDataRepository.deleteByModelIdAndTenantId(modelId, tenantId);
    }

    private ModelVersion toDomain(ModelVersionEntity entity) {
        return ModelVersion.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .versionNumber(entity.getVersionNumber())
                .description(entity.getDescription())
                .artifactPath(entity.getArtifactPath())
                .artifactSize(entity.getArtifactSize())
                .checksum(entity.getChecksum())
                .trainingMetrics(entity.getTrainingMetrics())
                .validationMetrics(entity.getValidationMetrics())
                .hyperparameters(entity.getHyperparameters())
                .gitCommitHash(entity.getGitCommitHash())
                .trainingDataSetId(entity.getTrainingDataSetId())
                .isProductionReady(entity.getIsProductionReady())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .deployedAt(entity.getDeployedAt())
                .version(entity.getVersion())
                .build();
    }

    private ModelVersionEntity toEntity(ModelVersion modelVersion) {
        return ModelVersionEntity.builder()
                .uuid(modelVersion.getId() != null ? modelVersion.getId() : UUID.randomUUID())
                .tenantId(modelVersion.getTenantId())
                .modelId(modelVersion.getModelId())
                .versionNumber(modelVersion.getVersionNumber())
                .description(modelVersion.getDescription())
                .artifactPath(modelVersion.getArtifactPath())
                .artifactSize(modelVersion.getArtifactSize())
                .checksum(modelVersion.getChecksum())
                .trainingMetrics(modelVersion.getTrainingMetrics())
                .validationMetrics(modelVersion.getValidationMetrics())
                .hyperparameters(modelVersion.getHyperparameters())
                .gitCommitHash(modelVersion.getGitCommitHash())
                .trainingDataSetId(modelVersion.getTrainingDataSetId())
                .isProductionReady(modelVersion.getIsProductionReady())
                .createdBy(modelVersion.getCreatedBy())
                .createdAt(modelVersion.getCreatedAt())
                .deployedAt(modelVersion.getDeployedAt())
                .version(modelVersion.getVersion())
                .build();
    }
}
