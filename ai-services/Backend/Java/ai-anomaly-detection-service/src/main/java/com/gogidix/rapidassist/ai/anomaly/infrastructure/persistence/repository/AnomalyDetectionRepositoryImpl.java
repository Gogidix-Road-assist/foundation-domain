package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyDetection;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalySeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyStatus;
import com.gogidix.rapidassist.ai.anomaly.domain.repository.AnomalyDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.AnomalyDetectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of AnomalyDetectionRepositoryPort.
 * Adapters between domain model and MongoDB entity.
 */
@Repository
@RequiredArgsConstructor
public class AnomalyDetectionRepositoryImpl implements AnomalyDetectionRepositoryPort {

    private final AnomalyDetectionMongoRepository mongoRepository;

    @Override
    public AnomalyDetection save(String tenantId, AnomalyDetection detection) {
        AnomalyDetectionEntity entity = toEntity(detection);
        entity.setTenantId(tenantId);
        AnomalyDetectionEntity savedEntity = mongoRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<AnomalyDetection> findById(String tenantId, UUID detectionId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(detectionId))
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<AnomalyDetection> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findByDataSource(String tenantId, String dataSource) {
        return mongoRepository.findByTenantIdAndDataSource(tenantId, dataSource).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findBySeverity(String tenantId, String severity) {
        AnomalySeverity severityEnum = AnomalySeverity.valueOf(severity.toUpperCase());
        return mongoRepository.findByTenantIdAndSeverity(tenantId, severityEnum).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findByStatus(String tenantId, String status) {
        AnomalyStatus statusEnum = AnomalyStatus.valueOf(status.toUpperCase());
        return mongoRepository.findByTenantIdAndStatus(tenantId, statusEnum).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findByDetectedAtBetween(String tenantId,  LocalDateTime startDate, LocalDateTime endDate) {
        return mongoRepository.findByTenantIdAndDetectedAtBetween(tenantId, startDate, endDate).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findPendingDetections(String tenantId) {
        return mongoRepository.findPendingDetections(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findCriticalDetections(String tenantId) {
        return mongoRepository.findCriticalDetections(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnomalyDetection> findByAnomalyScoreGreaterThan(String tenantId, Double threshold) {
        return mongoRepository.findByTenantIdAndAnomalyScoreGreaterThan(tenantId, threshold).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID detectionId) {
        mongoRepository.findByTenantId(tenantId).stream()
                .filter(entity -> entity.getUuid().equals(detectionId))
                .findFirst()
                .ifPresent(mongoRepository::delete);
    }

    @Override
    public boolean exists(String tenantId, UUID detectionId) {
        return mongoRepository.existsByTenantIdAndUuid(tenantId, detectionId);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    @Override
    public long countByStatus(String tenantId, String status) {
        AnomalyStatus statusEnum = AnomalyStatus.valueOf(status.toUpperCase());
        return mongoRepository.countByTenantIdAndStatus(tenantId, statusEnum);
    }

    /**
     * Convert domain model to entity
     */
    private AnomalyDetectionEntity toEntity(AnomalyDetection domain) {
        return AnomalyDetectionEntity.builder()
                .id(domain.getId() != null ? domain.getId().toString() : null)
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .dataSource(domain.getDataSource())
                .dataPoint(domain.getDataPoint())
                .severity(domain.getSeverity())
                .status(domain.getStatus())
                .anomalyScore(domain.getAnomalyScore())
                .confidence(domain.getConfidence())
                .detectionMethod(domain.getDetectionMethod())
                .data(domain.getData())
                .anomalyFeatures(domain.getAnomalyFeatures())
                .patternId(domain.getPatternId())
                .ruleId(domain.getRuleId())
                .detectedAt(domain.getDetectedAt())
                .acknowledgedAt(domain.getAcknowledgedAt())
                .acknowledgedBy(domain.getAcknowledgedBy())
                .resolvedAt(domain.getResolvedAt())
                .resolvedBy(domain.getResolvedBy())
                .description(domain.getDescription())
                .recommendation(domain.getRecommendation())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .version(domain.getVersion())
                .build();
    }

    /**
     * Convert entity to domain model
     */
    private AnomalyDetection toDomain(AnomalyDetectionEntity entity) {
        return AnomalyDetection.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .dataSource(entity.getDataSource())
                .dataPoint(entity.getDataPoint())
                .severity(entity.getSeverity())
                .status(entity.getStatus())
                .anomalyScore(entity.getAnomalyScore())
                .confidence(entity.getConfidence())
                .detectionMethod(entity.getDetectionMethod())
                .data(entity.getData())
                .anomalyFeatures(entity.getAnomalyFeatures())
                .patternId(entity.getPatternId())
                .ruleId(entity.getRuleId())
                .detectedAt(entity.getDetectedAt())
                .acknowledgedAt(entity.getAcknowledgedAt())
                .acknowledgedBy(entity.getAcknowledgedBy())
                .resolvedAt(entity.getResolvedAt())
                .resolvedBy(entity.getResolvedBy())
                .description(entity.getDescription())
                .recommendation(entity.getRecommendation())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }
}
