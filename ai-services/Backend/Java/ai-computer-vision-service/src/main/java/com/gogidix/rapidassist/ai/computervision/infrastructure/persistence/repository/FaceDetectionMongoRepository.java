package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.FaceDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for FaceDetection
 */
@Repository
public interface FaceDetectionMongoRepository extends MongoRepository<FaceDetectionEntity, String> {

    // All queries MUST include tenant filtering for multi-tenancy
    Optional<FaceDetectionEntity> findByTenantIdAndUuid(String tenantId, UUID uuid);

    List<FaceDetectionEntity> findByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    List<FaceDetectionEntity> findByTenantId(String tenantId);

    List<FaceDetectionEntity> findByTenantIdAndEmotion(String tenantId, String emotion);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    long countByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);
}
