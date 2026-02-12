package com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModel;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType;
import com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.ForecastModelRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.persistence.entity.ForecastModelEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ForecastModelRepositoryPort.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ForecastModelRepositoryImpl implements ForecastModelRepositoryPort {

    private final SpringDataForecastModelRepository springDataRepository;

    @Override
    public ForecastModel save(String tenantId, ForecastModel model) {
        log.info("Saving model: {} for tenant: {}", model.getId(), tenantId);
        ForecastModelEntity entity = toEntity(model);
        entity.setTenantId(tenantId);
        ForecastModelEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ForecastModel> findById(String tenantId, UUID modelId) {
        return springDataRepository.findByUuidAndTenantId(modelId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ForecastModel> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ForecastModel> findByModelType(String tenantId, ForecastModelType modelType) {
        return springDataRepository.findByModelTypeAndTenantId(modelType, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ForecastModel> findByStatus(String tenantId, ModelStatus status) {
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ForecastModel> findDeployedModels(String tenantId) {
        return springDataRepository.findByStatusAndTenantId(ModelStatus.DEPLOYED, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ForecastModel> findTrainedModels(String tenantId) {
        return springDataRepository.findByStatusAndTenantId(ModelStatus.TRAINED, tenantId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void updateStatus(String tenantId, UUID modelId, ModelStatus status) {
        springDataRepository.findByUuidAndTenantId(modelId, tenantId).ifPresent(entity -> {
            entity.setStatus(status);
            springDataRepository.save(entity);
        });
    }

    @Override
    public void delete(String tenantId, UUID modelId) {
        springDataRepository.deleteByUuidAndTenantId(modelId, tenantId);
    }

    @Override
    public boolean exists(String tenantId, UUID modelId) {
        return springDataRepository.existsByUuidAndTenantId(modelId, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    private ForecastModel toDomain(ForecastModelEntity entity) {
        return ForecastModel.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelName(entity.getModelName())
                .description(entity.getDescription())
                .modelType(entity.getModelType())
                .status(entity.getStatus())
                .version(entity.getVersion())
                .trainingDataPoints(entity.getTrainingDataPoints())
                .trainingAccuracy(entity.getTrainingAccuracy())
                .validationAccuracy(entity.getValidationAccuracy())
                .testAccuracy(entity.getTestAccuracy())
                .lastTrainedAt(entity.getLastTrainedAt())
                .deployedAt(entity.getDeployedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private ForecastModelEntity toEntity(ForecastModel domain) {
        return ForecastModelEntity.builder()
                .uuid(domain.getId() != null ? domain.getId() : UUID.randomUUID())
                .tenantId(domain.getTenantId())
                .modelName(domain.getModelName())
                .description(domain.getDescription())
                .modelType(domain.getModelType())
                .status(domain.getStatus())
                .version(domain.getVersion())
                .trainingDataPoints(domain.getTrainingDataPoints())
                .trainingAccuracy(domain.getTrainingAccuracy())
                .validationAccuracy(domain.getValidationAccuracy())
                .testAccuracy(domain.getTestAccuracy())
                .lastTrainedAt(domain.getLastTrainedAt())
                .deployedAt(domain.getDeployedAt())
                .createdAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .build();
    }
}
