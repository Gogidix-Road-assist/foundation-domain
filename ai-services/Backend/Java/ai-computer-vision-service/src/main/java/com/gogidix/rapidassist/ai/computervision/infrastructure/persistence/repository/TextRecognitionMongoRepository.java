package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.TextRecognitionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for TextRecognition
 */
@Repository
public interface TextRecognitionMongoRepository extends MongoRepository<TextRecognitionEntity, String> {

    // All queries MUST include tenant filtering for multi-tenancy
    Optional<TextRecognitionEntity> findByTenantIdAndUuid(String tenantId, UUID uuid);

    List<TextRecognitionEntity> findByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    List<TextRecognitionEntity> findByTenantId(String tenantId);

    List<TextRecognitionEntity> findByTenantIdAndLanguage(String tenantId, String language);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    long countByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);
}
