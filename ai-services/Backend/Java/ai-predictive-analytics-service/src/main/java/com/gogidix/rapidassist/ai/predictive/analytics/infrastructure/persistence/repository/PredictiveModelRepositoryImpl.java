package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictiveModel;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.PredictiveModelRepositoryPort;
import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.PredictiveModelEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PredictiveModelRepositoryPort using MongoDB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PredictiveModelRepositoryImpl implements PredictiveModelRepositoryPort {

    private final SpringDataPredictiveModelRepository springDataRepository;

    @Override
    public PredictiveModel save(PredictiveModel model) {
        PredictiveModelEntity entity = toEntity(model);
        PredictiveModelEntity savedEntity = springDataRepository.save(entity);
        log.debug("Saved predictive model with UUID: {}", savedEntity.getUuid());
        return toDomain(savedEntity);
    }

    @Override
    public Optional<PredictiveModel> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public Optional<PredictiveModel> findByNameAndTenantId(String name, String tenantId) {
        return springDataRepository.findByNameAndTenantId(name, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<PredictiveModel> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PredictiveModel> findByStatusAndTenantId(ModelStatus status, String tenantId) {
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PredictiveModel> findByModelTypeAndTenantId(ModelType modelType, String tenantId) {
        return springDataRepository.findByModelTypeAndTenantId(modelType, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<PredictiveModel> findActiveModelsByTenantId(String tenantId) {
        return springDataRepository.findActiveModelsByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
        log.debug("Deleted predictive model with UUID: {}", id);
    }

    @Override
    public boolean existsByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public List<PredictiveModel> findModelsNeedingRetraining(String tenantId, int thresholdDays) {
        LocalDateTime threshold = LocalDateTime.now().minusDays(thresholdDays);
        return springDataRepository.findModelsNeedingRetraining(tenantId, threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Convert domain model to entity
     */
    private PredictiveModelEntity toEntity(PredictiveModel domain) {
        // Simplified conversion - in production, use MapStruct
        return PredictiveModelEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .modelType(domain.getModelType())
                .status(domain.getStatus())
                .algorithm(domain.getAlgorithm())
                .performanceMetrics(domain.getPerformanceMetrics())
                .modelVersion(domain.getModelVersion())
                .featureCount(domain.getFeatureCount())
                .targetVariable(domain.getTargetVariable())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .lastTrainedAt(domain.getLastTrainedAt())
                .deployedAt(domain.getDeployedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .modelStoragePath(domain.getModelStoragePath())
                .metadata(domain.getMetadata())
                .build();
    }

    /**
     * Convert entity to domain model
     */
    private PredictiveModel toDomain(PredictiveModelEntity entity) {
        return PredictiveModel.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .modelType(entity.getModelType())
                .status(entity.getStatus())
                .algorithm(entity.getAlgorithm())
                .performanceMetrics(entity.getPerformanceMetrics())
                .modelVersion(entity.getModelVersion())
                .featureCount(entity.getFeatureCount())
                .targetVariable(entity.getTargetVariable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .lastTrainedAt(entity.getLastTrainedAt())
                .deployedAt(entity.getDeployedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .modelStoragePath(entity.getModelStoragePath())
                .metadata(entity.getMetadata())
                .build();
    }
}
