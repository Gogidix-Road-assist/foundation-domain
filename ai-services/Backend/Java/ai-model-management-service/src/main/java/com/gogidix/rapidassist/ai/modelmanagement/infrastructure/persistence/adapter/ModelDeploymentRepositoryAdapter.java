package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.DeploymentStatus;
import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelDeployment;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.ModelDeploymentRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelDeploymentEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataModelDeploymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing ModelDeploymentRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ModelDeploymentRepositoryAdapter implements ModelDeploymentRepositoryPort {

    private final SpringDataModelDeploymentRepository springDataRepository;

    @Override
    public ModelDeployment save(String tenantId, ModelDeployment deployment) {
        ModelDeploymentEntity entity = toEntity(deployment);
        entity.setTenantId(tenantId);
        ModelDeploymentEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ModelDeployment> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ModelDeployment> findByModelId(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantId(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelDeployment> findByEnvironment(String tenantId, String environment) {
        return springDataRepository.findByTenantIdAndEnvironment(tenantId, environment).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelDeployment> findByStatus(String tenantId, DeploymentStatus status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelDeployment> findActiveDeployments(String tenantId, String environment) {
        return springDataRepository.findByTenantIdAndEnvironmentAndStatus(
                tenantId, environment, DeploymentStatus.DEPLOYED).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ModelDeployment> findByModelVersionId(String tenantId, UUID modelVersionId) {
        return springDataRepository.findByModelVersionIdAndTenantId(modelVersionId, tenantId)
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

    private ModelDeployment toDomain(ModelDeploymentEntity entity) {
        return ModelDeployment.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .modelVersionId(entity.getModelVersionId())
                .environment(entity.getEnvironment())
                .status(entity.getStatus())
                .endpointUrl(entity.getEndpointUrl())
                .instanceCount(entity.getInstanceCount())
                .cpuUnits(entity.getCpuUnits())
                .memoryMB(entity.getMemoryMB())
                .acceleratorType(entity.getAcceleratorType())
                .acceleratorCount(entity.getAcceleratorCount())
                .environmentVariables(entity.getEnvironmentVariables())
                .deploymentStrategy(entity.getDeploymentStrategy())
                .deploymentConfig(entity.getDeploymentConfig())
                .deployedBy(entity.getDeployedBy())
                .deployedAt(entity.getDeployedAt())
                .updatedAt(entity.getUpdatedAt())
                .healthStatus(entity.getHealthStatus())
                .currentRequests(entity.getCurrentRequests())
                .totalRequests(entity.getTotalRequests())
                .averageResponseTime(entity.getAverageResponseTime())
                .version(entity.getVersion())
                .build();
    }

    private ModelDeploymentEntity toEntity(ModelDeployment deployment) {
        return ModelDeploymentEntity.builder()
                .uuid(deployment.getId() != null ? deployment.getId() : UUID.randomUUID())
                .tenantId(deployment.getTenantId())
                .modelId(deployment.getModelId())
                .modelVersionId(deployment.getModelVersionId())
                .environment(deployment.getEnvironment())
                .status(deployment.getStatus())
                .endpointUrl(deployment.getEndpointUrl())
                .instanceCount(deployment.getInstanceCount())
                .cpuUnits(deployment.getCpuUnits())
                .memoryMB(deployment.getMemoryMB())
                .acceleratorType(deployment.getAcceleratorType())
                .acceleratorCount(deployment.getAcceleratorCount())
                .environmentVariables(deployment.getEnvironmentVariables())
                .deploymentStrategy(deployment.getDeploymentStrategy())
                .deploymentConfig(deployment.getDeploymentConfig())
                .deployedBy(deployment.getDeployedBy())
                .deployedAt(deployment.getDeployedAt())
                .updatedAt(deployment.getUpdatedAt())
                .healthStatus(deployment.getHealthStatus())
                .currentRequests(deployment.getCurrentRequests())
                .totalRequests(deployment.getTotalRequests())
                .averageResponseTime(deployment.getAverageResponseTime())
                .version(deployment.getVersion())
                .build();
    }
}
