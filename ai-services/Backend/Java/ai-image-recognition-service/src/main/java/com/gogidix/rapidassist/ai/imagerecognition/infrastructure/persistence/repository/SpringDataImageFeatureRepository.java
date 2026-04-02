package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.ImageFeatureEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ImageFeatureEntity.
 */
@Repository
public interface SpringDataImageFeatureRepository extends MongoRepository<ImageFeatureEntity, String> {

    /**
     * Find by image recognition ID and tenant.
     */
    List<ImageFeatureEntity> findByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);

    /**
     * Delete by image recognition ID and tenant.
     */
    void deleteByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);
}
