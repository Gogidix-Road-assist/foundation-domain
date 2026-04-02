package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.adapter;

import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.repository.ImageAnalysisRepositoryPort;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ImageAnalysisEntity;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository.ImageAnalysisMongoRepository;
import com.gogidix.rapidassist.ai.computervision.infrastructure.tenant.RequestContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * MongoDB implementation of ImageAnalysisRepositoryPort
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageAnalysisRepositoryAdapter implements ImageAnalysisRepositoryPort {

    private final ImageAnalysisMongoRepository mongoRepository;

    @Override
    public ImageAnalysisAggregate save(ImageAnalysisAggregate aggregate) {
        ImageAnalysisEntity entity = toEntity(aggregate);
        ImageAnalysisEntity saved = mongoRepository.save(entity);
        return toAggregate(saved);
    }

    @Override
    public Optional<ImageAnalysisAggregate> findById(UUID id) {
        String tenantId = RequestContextHolder.getTenantId();
        return mongoRepository.findByTenantIdAndUuid(tenantId, id)
                .map(this::toAggregate);
    }

    @Override
    public Optional<ImageAnalysisAggregate> findByTenantIdAndId(String tenantId, UUID id) {
        return mongoRepository.findByTenantIdAndUuid(tenantId, id)
                .map(this::toAggregate);
    }

    @Override
    public List<ImageAnalysisAggregate> findByTenantId(String tenantId) {
        return mongoRepository.findByTenantId(tenantId).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageAnalysisAggregate> findByUserId(String userId) {
        String tenantId = RequestContextHolder.getTenantId();
        return mongoRepository.findByTenantIdAndUserId(tenantId, userId).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageAnalysisAggregate> findByStatus(String status) {
        String tenantId = RequestContextHolder.getTenantId();
        return mongoRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageAnalysisAggregate> findByTenantIdAndStatus(String tenantId, String status) {
        return mongoRepository.findByTenantIdAndStatus(tenantId, status).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public List<ImageAnalysisAggregate> findByAnalysisType(String analysisType) {
        String tenantId = RequestContextHolder.getTenantId();
        return mongoRepository.findByTenantIdAndAnalysisType(tenantId, analysisType).stream()
                .map(this::toAggregate)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        String tenantId = RequestContextHolder.getTenantId();
        mongoRepository.deleteByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public boolean existsById(UUID id) {
        String tenantId = RequestContextHolder.getTenantId();
        return mongoRepository.existsByTenantIdAndUuid(tenantId, id);
    }

    @Override
    public long countByTenantId(String tenantId) {
        return mongoRepository.countByTenantId(tenantId);
    }

    private ImageAnalysisEntity toEntity(ImageAnalysisAggregate aggregate) {
        return ImageAnalysisEntity.builder()
                .uuid(aggregate.getId())
                .tenantId(aggregate.getTenantId())
                .userId(aggregate.getUserId())
                .imageUrl(aggregate.getImageUrl())
                .imageStoragePath(aggregate.getImageStoragePath())
                .format(aggregate.getFormat())
                .fileSize(aggregate.getFileSize())
                .width(aggregate.getWidth())
                .height(aggregate.getHeight())
                .status(aggregate.getStatus())
                .analysisType(aggregate.getAnalysisType())
                .overallConfidence(aggregate.getOverallConfidence())
                .errorMessage(aggregate.getErrorMessage())
                .createdAt(aggregate.getCreatedAt())
                .updatedAt(aggregate.getUpdatedAt())
                .completedAt(aggregate.getCompletedAt())
                .createdBy(aggregate.getCreatedBy())
                .updatedBy(aggregate.getUpdatedBy())
                .processingTimeMs(aggregate.getProcessingTimeMs())
                .version(System.currentTimeMillis())
                .build();
    }

    private ImageAnalysisAggregate toAggregate(ImageAnalysisEntity entity) {
        return ImageAnalysisAggregate.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .userId(entity.getUserId())
                .imageUrl(entity.getImageUrl())
                .imageStoragePath(entity.getImageStoragePath())
                .format(entity.getFormat())
                .fileSize(entity.getFileSize())
                .width(entity.getWidth())
                .height(entity.getHeight())
                .status(entity.getStatus())
                .analysisType(entity.getAnalysisType())
                .overallConfidence(entity.getOverallConfidence())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .completedAt(entity.getCompletedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .processingTimeMs(entity.getProcessingTimeMs())
                .detectedObjects(List.of())
                .detectedFaces(List.of())
                .recognizedTexts(List.of())
                .classifications(List.of())
                .build();
    }
}
