package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.repository.FraudDetectionRepositoryPort;
import com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.entity.FraudDetectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB adapter implementation for FraudDetectionRepositoryPort
 */
@Repository
@RequiredArgsConstructor
public class FraudDetectionRepositoryAdapter implements FraudDetectionRepositoryPort {

    private final SpringDataFraudDetectionRepository springDataRepository;

    @Override
    public FraudDetection save(String tenantId, FraudDetection detection) {
        FraudDetectionEntity entity = toEntity(detection);
        FraudDetectionEntity saved = springDataRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<FraudDetection> findById(String tenantId, UUID id) {
        return springDataRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public List<FraudDetection> findByTenantId(String tenantId) {
        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudDetection> findByEntityId(String tenantId, String entityId) {
        return springDataRepository.findByTenantIdAndEntityId(tenantId, entityId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudDetection> findByRiskLevel(String tenantId, String riskLevel) {
        return springDataRepository.findByTenantIdAndRiskLevel(
                tenantId, com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel.valueOf(riskLevel)
        ).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudDetection> findByStatus(String tenantId, String status) {
        return springDataRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FraudDetection> findPendingReview(String tenantId) {
        return springDataRepository.findByTenantIdAndRequiresReviewTrue(tenantId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String tenantId, UUID id) {
        springDataRepository.deleteById(id);
    }

    @Override
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsById(id);
    }

    private FraudDetectionEntity toEntity(FraudDetection domain) {
        return FraudDetectionEntity.builder()
                .id(domain.getId())
                .tenantId(domain.getTenantId())
                .entityType(domain.getEntityType())
                .entityId(domain.getEntityId())
                .riskLevel(domain.getRiskLevel())
                .riskScore(domain.getRiskScore())
                .detectionMethod(domain.getDetectionMethod())
                .detectionDetails(domain.getDetectionDetails())
                .detectedPatterns(domain.getDetectedPatterns())
                .status(domain.getStatus())
                .requiresReview(domain.getRequiresReview())
                .assignedTo(domain.getAssignedTo())
                .detectedAt(domain.getDetectedAt())
                .reviewedAt(domain.getReviewedAt())
                .reviewedBy(domain.getReviewedBy())
                .reviewNotes(domain.getReviewNotes())
                .metadata(domain.getMetadata())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    private FraudDetection toDomain(FraudDetectionEntity entity) {
        return FraudDetection.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .riskLevel(entity.getRiskLevel())
                .riskScore(entity.getRiskScore())
                .detectionMethod(entity.getDetectionMethod())
                .detectionDetails(entity.getDetectionDetails())
                .detectedPatterns(entity.getDetectedPatterns())
                .status(entity.getStatus())
                .requiresReview(entity.getRequiresReview())
                .assignedTo(entity.getAssignedTo())
                .detectedAt(entity.getDetectedAt())
                .reviewedAt(entity.getReviewedAt())
                .reviewedBy(entity.getReviewedBy())
                .reviewNotes(entity.getReviewNotes())
                .metadata(entity.getMetadata())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
