package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.*;
import com.gogidix.rapidassist.ai.imagerecognition.domain.repository.ImageRecognitionRepositoryPort;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.ImageRecognitionEntity;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.mapper.ImageRecognitionPersistenceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ImageRecognitionRepositoryPort.
 * Adapters domain repository port to Spring Data MongoDB infrastructure.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ImageRecognitionRepositoryImpl implements ImageRecognitionRepositoryPort {

    private final SpringDataImageRecognitionRepository springDataRepository;
    private final SpringDataRecognizedObjectRepository objectRepository;
    private final SpringDataSceneLabelRepository labelRepository;
    private final SpringDataBrandDetectionRepository brandRepository;
    private final SpringDataImageFeatureRepository featureRepository;
    private final ImageRecognitionPersistenceMapper persistenceMapper;

    @Override
    @Transactional
    public ImageRecognition save(String tenantId, ImageRecognition imageRecognition) {
        log.info("Saving image recognition: {} for tenant: {}", imageRecognition.getId(), tenantId);

        // Convert domain to entity
        ImageRecognitionEntity entity = persistenceMapper.toEntity(imageRecognition);

        // Save entity
        ImageRecognitionEntity savedEntity = springDataRepository.save(entity);

        // Save child entities
        saveChildEntities(tenantId, imageRecognition);

        // Convert back to domain with child entities
        return loadImageRecognitionWithChildren(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageRecognition> findById(String tenantId, UUID id) {
        log.info("Finding image recognition by ID: {} for tenant: {}", id, tenantId);

        return springDataRepository.findByUuidAndTenantId(id, tenantId)
                .map(this::loadImageRecognitionWithChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ImageRecognition> findByRequestId(String tenantId, String requestId) {
        log.info("Finding image recognition by request ID: {} for tenant: {}", requestId, tenantId);

        return springDataRepository.findByRequestIdAndTenantId(requestId, tenantId)
                .map(this::loadImageRecognitionWithChildren);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognition> findByTenantId(String tenantId) {
        log.info("Finding all image recognitions for tenant: {}", tenantId);

        return springDataRepository.findByTenantId(tenantId).stream()
                .map(this::loadImageRecognitionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognition> findByUserId(String tenantId, String userId) {
        log.info("Finding image recognitions for user: {} in tenant: {}", userId, tenantId);

        return springDataRepository.findByUserIdAndTenantId(userId, tenantId).stream()
                .map(this::loadImageRecognitionWithChildren)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ImageRecognition> findByStatus(String tenantId, String status) {
        log.info("Finding image recognitions by status: {} for tenant: {}", status, tenantId);

        RecognitionStatus recognitionStatus = RecognitionStatus.valueOf(status);

        return springDataRepository.findByStatusAndTenantId(recognitionStatus, tenantId).stream()
                .map(this::loadImageRecognitionWithChildren)
                .toList();
    }

    @Override
    @Transactional
    public void delete(String tenantId, UUID id) {
        log.info("Deleting image recognition: {} for tenant: {}", id, tenantId);

        // Delete child entities first
        objectRepository.deleteByImageRecognitionIdAndTenantId(id, tenantId);
        labelRepository.deleteByImageRecognitionIdAndTenantId(id, tenantId);
        brandRepository.deleteByImageRecognitionIdAndTenantId(id, tenantId);
        featureRepository.deleteByImageRecognitionIdAndTenantId(id, tenantId);

        // Delete main entity
        springDataRepository.deleteByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(String tenantId, UUID id) {
        return springDataRepository.existsByUuidAndTenantId(id, tenantId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTenantId(String tenantId) {
        return springDataRepository.countByTenantId(tenantId);
    }

    /**
     * Save all child entities.
     */
    private void saveChildEntities(String tenantId, ImageRecognition imageRecognition) {
        UUID recognitionId = imageRecognition.getId();

        // Save recognized objects
        for (RecognizedObject obj : imageRecognition.getRecognizedObjects()) {
            obj.setTenantId(tenantId);
            obj.setImageRecognitionId(recognitionId);
            objectRepository.save(persistenceMapper.toEntity(obj));
        }

        // Save scene labels
        for (SceneLabel label : imageRecognition.getSceneLabels()) {
            label.setTenantId(tenantId);
            label.setImageRecognitionId(recognitionId);
            labelRepository.save(persistenceMapper.toEntity(label));
        }

        // Save brand detections
        for (BrandDetection brand : imageRecognition.getBrandDetections()) {
            brand.setTenantId(tenantId);
            brand.setImageRecognitionId(recognitionId);
            brandRepository.save(persistenceMapper.toEntity(brand));
        }

        // Save image features
        for (ImageFeature feature : imageRecognition.getImageFeatures()) {
            feature.setTenantId(tenantId);
            feature.setImageRecognitionId(recognitionId);
            featureRepository.save(persistenceMapper.toEntity(feature));
        }
    }

    /**
     * Load image recognition with all child entities.
     */
    private ImageRecognition loadImageRecognitionWithChildren(ImageRecognitionEntity entity) {
        // Convert to domain
        ImageRecognition imageRecognition = persistenceMapper.toDomain(entity);

        UUID recognitionId = entity.getUuid();
        String tenantId = entity.getTenantId();

        // Load recognized objects
        List<RecognizedObject> objects = objectRepository
                .findByImageRecognitionIdAndTenantId(recognitionId, tenantId)
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        imageRecognition.setRecognizedObjects(objects);

        // Load scene labels
        List<SceneLabel> labels = labelRepository
                .findByImageRecognitionIdAndTenantId(recognitionId, tenantId)
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        imageRecognition.setSceneLabels(labels);

        // Load brand detections
        List<BrandDetection> brands = brandRepository
                .findByImageRecognitionIdAndTenantId(recognitionId, tenantId)
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        imageRecognition.setBrandDetections(brands);

        // Load image features
        List<ImageFeature> features = featureRepository
                .findByImageRecognitionIdAndTenantId(recognitionId, tenantId)
                .stream()
                .map(persistenceMapper::toDomain)
                .toList();
        imageRecognition.setImageFeatures(features);

        return imageRecognition;
    }
}
