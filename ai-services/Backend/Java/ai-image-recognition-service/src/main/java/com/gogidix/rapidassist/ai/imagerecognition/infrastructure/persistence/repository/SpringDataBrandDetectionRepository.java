package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.BrandDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for BrandDetectionEntity.
 */
@Repository
public interface SpringDataBrandDetectionRepository extends MongoRepository<BrandDetectionEntity, String> {

    /**
     * Find by image recognition ID and tenant.
     */
    List<BrandDetectionEntity> findByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);

    /**
     * Delete by image recognition ID and tenant.
     */
    void deleteByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);
}
