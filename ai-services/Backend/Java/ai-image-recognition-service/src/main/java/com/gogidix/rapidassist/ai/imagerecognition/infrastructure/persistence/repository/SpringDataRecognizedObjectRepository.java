package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.RecognizedObjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RecognizedObjectEntity.
 */
@Repository
public interface SpringDataRecognizedObjectRepository extends MongoRepository<RecognizedObjectEntity, String> {

    /**
     * Find by image recognition ID and tenant.
     */
    List<RecognizedObjectEntity> findByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);

    /**
     * Delete by image recognition ID and tenant.
     */
    void deleteByImageRecognitionIdAndTenantId(UUID imageRecognitionId, String tenantId);
}
