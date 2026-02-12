package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.Model;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelType;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.ModelRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing ModelRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ModelRepositoryAdapter implements ModelRepositoryPort {

    private final SpringDataModelRepository springDataRepository;

    @Override
    public Model save(String tenantId, Model model) {
        ModelEntity entity = toEntity(model);
        entity.setTenantId(tenantId);
        ModelEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Model> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<Model> findAll(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Model> findByStatus(String tenantId, ModelStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Model> findByModelType(String tenantId, ModelType modelType) {
        return springDataRepository.findByTenantIdAndModelType(tenantId, modelType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Model> findByName(String tenantId, String name) {
        return springDataRepository.findByTenantIdAndName(tenantId, name)
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

    private Model toDomain(ModelEntity entity) {
        return Model.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .modelType(entity.getModelType())
                .status(entity.getStatus())
                .framework(entity.getFramework())
                .version(entity.getVersion())
                .hyperparameters(entity.getHyperparameters())
                .metadata(entity.getMetadata())
                .artifactPath(entity.getArtifactPath())
                .configurationPath(entity.getConfigurationPath())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastDeployedAt(entity.getLastDeployedAt())
                .version(entity.getVersion())
                .build();
    }

    private ModelEntity toEntity(Model model) {
        return ModelEntity.builder()
                .uuid(model.getId() != null ? model.getId() : UUID.randomUUID())
                .tenantId(model.getTenantId())
                .name(model.getName())
                .description(model.getDescription())
                .modelType(model.getModelType())
                .status(model.getStatus())
                .framework(model.getFramework())
                .version(model.getVersion())
                .hyperparameters(model.getHyperparameters())
                .metadata(model.getMetadata())
                .artifactPath(model.getArtifactPath())
                .configurationPath(model.getConfigurationPath())
                .createdBy(model.getCreatedBy())
                .updatedBy(model.getUpdatedBy())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .lastDeployedAt(model.getLastDeployedAt())
                .version(model.getVersion())
                .build();
    }
}
