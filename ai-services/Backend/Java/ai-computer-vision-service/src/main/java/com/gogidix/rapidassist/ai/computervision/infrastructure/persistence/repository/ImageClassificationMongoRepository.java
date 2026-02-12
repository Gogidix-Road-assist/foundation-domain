package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ImageClassificationEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ImageClassification
 */
@Repository
public interface ImageClassificationMongoRepository extends MongoRepository<ImageClassificationEntity, String> {

    // All queries MUST include tenant filtering for multi-tenancy
    Optional<ImageClassificationEntity> findByTenantIdAndUuid(String tenantId, UUID uuid);

    List<ImageClassificationEntity> findByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    List<ImageClassificationEntity> findByTenantId(String tenantId);

    List<ImageClassificationEntity> findByTenantIdAndPrimaryClass(String tenantId, String primaryClass);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    long countByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);
}
