package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Prediction;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.PredictionRepositoryPort;
import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.PredictionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of PredictionRepositoryPort using MongoDB.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PredictionRepositoryImpl implements PredictionRepositoryPort {

    private final SpringDataPredictionRepository springDataRepository;

    @Override
    public Prediction save(Prediction prediction) {
        PredictionEntity entity = toEntity(prediction);
        PredictionEntity savedEntity = springDataRepository.save(entity);
        log.debug("Saved prediction with UUID: {}", savedEntity.getUuid());
        return toDomain(savedEntity);
    }

    @Override
    public Optional<Prediction> findByIdAndTenantId(UUID id, String tenantId) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<Prediction> findByModelIdAndTenantId(UUID modelId, String tenantId) {
        return springDataRepository.findByModelIdAndTenantId(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prediction> findByStatusAndTenantId(PredictionStatus status, String tenantId) {
        return springDataRepository.findByStatusAndTenantId(status, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prediction> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        return springDataRepository.findByCreatedAtBetweenAndTenantId(startDate, endDate, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prediction> findRecentPredictionsByTenantId(String tenantId, int limit) {
        List<PredictionEntity> entities = springDataRepository.findByTenantId(tenantId);
        return entities.stream()
                .limit(limit)
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Prediction> findPendingPredictionsByTenantId(String tenantId) {
        return springDataRepository.findPendingPredictionsByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndTenantId(UUID id, String tenantId) {
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
        log.debug("Deleted prediction with UUID: {}", id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByModelIdAndTenantId(UUID modelId, String tenantId) {
        return springDataRepository.countByModelIdAndTenantId(modelId, tenantId);
    }

    @Override
    public void deleteOldPredictions(LocalDateTime cutoffDate) {
        springDataRepository.deleteByCompletedAtBefore(cutoffDate);
        log.debug("Deleted old predictions completed before: {}", cutoffDate);
    }

    /**
     * Convert domain model to entity
     */
    private PredictionEntity toEntity(Prediction domain) {
        return PredictionEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .modelId(domain.getModelId())
                .modelName(domain.getModelName())
                .inputData(domain.getInputData())
                .preprocessedData(domain.getPreprocessedData())
                .predictionResult(domain.getPredictionResult())
                .confidenceScore(domain.getConfidenceScore())
                .classProbabilities(domain.getClassProbabilities())
                .status(domain.getStatus())
                .errorMessage(domain.getErrorMessage())
                .createdAt(domain.getCreatedAt())
                .processedAt(domain.getProcessedAt())
                .completedAt(domain.getCompletedAt())
                .processingTimeMs(domain.getProcessingTimeMs())
                .metadata(domain.getMetadata())
                .build();
    }

    /**
     * Convert entity to domain model
     */
    private Prediction toDomain(PredictionEntity entity) {
        return Prediction.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .modelName(entity.getModelName())
                .inputData(entity.getInputData())
                .preprocessedData(entity.getPreprocessedData())
                .predictionResult(entity.getPredictionResult())
                .confidenceScore(entity.getConfidenceScore())
                .classProbabilities(entity.getClassProbabilities())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .processedAt(entity.getProcessedAt())
                .completedAt(entity.getCompletedAt())
                .processingTimeMs(entity.getProcessingTimeMs())
                .metadata(entity.getMetadata())
                .build();
    }
}
