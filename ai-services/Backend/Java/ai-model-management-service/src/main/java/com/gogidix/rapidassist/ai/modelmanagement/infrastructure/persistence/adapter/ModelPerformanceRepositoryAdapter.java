package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelPerformance;
import com.gogidix.rapidassist.ai.modelmanagement.domain.repository.ModelPerformanceRepositoryPort;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelPerformanceEntity;
import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository.SpringDataModelPerformanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Repository adapter implementing ModelPerformanceRepositoryPort.
 */
@Component
@RequiredArgsConstructor
public class ModelPerformanceRepositoryAdapter implements ModelPerformanceRepositoryPort {

    private final SpringDataModelPerformanceRepository springDataRepository;

    @Override
    public ModelPerformance save(String tenantId, ModelPerformance performance) {
        ModelPerformanceEntity entity = toEntity(performance);
        entity.setTenantId(tenantId);
        ModelPerformanceEntity savedEntity = springDataRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<ModelPerformance> findById(String tenantId, UUID id) {
        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::toDomain);
    }

    @Override
    public List<ModelPerformance> findByModelId(String tenantId, UUID modelId) {
        return springDataRepository.findByModelIdAndTenantIdOrderByTimestampDesc(modelId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelPerformance> findByModelVersionId(String tenantId, UUID modelVersionId) {
        return springDataRepository.findByModelVersionIdAndTenantIdOrderByTimestampDesc(modelVersionId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelPerformance> findByDeploymentId(String tenantId, UUID deploymentId) {
        return springDataRepository.findByDeploymentIdAndTenantIdOrderByTimestampDesc(deploymentId, tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelPerformance> findByEvaluationType(String tenantId, UUID modelId, String evaluationType) {
        return springDataRepository.findByModelIdAndTenantIdAndEvaluationTypeOrderByTimestampDesc(
                modelId, tenantId, evaluationType).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<ModelPerformance> findByTimeRange(String tenantId, UUID modelId, LocalDateTime startTime, LocalDateTime endTime) {
        return springDataRepository.findByModelIdAndTenantIdAndTimestampBetweenOrderByTimestampDesc(
                modelId, tenantId, startTime, endTime).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ModelPerformance> findLatest(String tenantId, UUID modelId) {
        return springDataRepository.findFirstByModelIdAndTenantIdOrderByTimestampDesc(modelId, tenantId)
                .map(this::toDomain);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    public void deleteByModelVersionId(String tenantId, UUID modelVersionId) {
        springDataRepository.deleteByModelVersionIdAndTenantId(modelVersionId, tenantId);
    }

    private ModelPerformance toDomain(ModelPerformanceEntity entity) {
        return ModelPerformance.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .modelId(entity.getModelId())
                .modelVersionId(entity.getModelVersionId())
                .deploymentId(entity.getDeploymentId())
                .accuracy(entity.getAccuracy())
                .precision(entity.getPrecision())
                .recall(entity.getRecall())
                .f1Score(entity.getF1Score())
                .auc(entity.getAuc())
                .latency(entity.getLatency())
                .throughput(entity.getThroughput())
                .errorRate(entity.getErrorRate())
                .requestCount(entity.getRequestCount())
                .successCount(entity.getSuccessCount())
                .failureCount(entity.getFailureCount())
                .avgResponseTime(entity.getAvgResponseTime())
                .p95ResponseTime(entity.getP95ResponseTime())
                .p99ResponseTime(entity.getP99ResponseTime())
                .customMetrics(entity.getCustomMetrics())
                .timestamp(entity.getTimestamp())
                .evaluationType(entity.getEvaluationType())
                .version(entity.getVersion())
                .build();
    }

    private ModelPerformanceEntity toEntity(ModelPerformance performance) {
        return ModelPerformanceEntity.builder()
                .uuid(performance.getId() != null ? performance.getId() : UUID.randomUUID())
                .tenantId(performance.getTenantId())
                .modelId(performance.getModelId())
                .modelVersionId(performance.getModelVersionId())
                .deploymentId(performance.getDeploymentId())
                .accuracy(performance.getAccuracy())
                .precision(performance.getPrecision())
                .recall(performance.getRecall())
                .f1Score(performance.getF1Score())
                .auc(performance.getAuc())
                .latency(performance.getLatency())
                .throughput(performance.getThroughput())
                .errorRate(performance.getErrorRate())
                .requestCount(performance.getRequestCount())
                .successCount(performance.getSuccessCount())
                .failureCount(performance.getFailureCount())
                .avgResponseTime(performance.getAvgResponseTime())
                .p95ResponseTime(performance.getP95ResponseTime())
                .p99ResponseTime(performance.getP99ResponseTime())
                .customMetrics(performance.getCustomMetrics())
                .timestamp(performance.getTimestamp())
                .evaluationType(performance.getEvaluationType())
                .version(performance.getVersion())
                .build();
    }
}
