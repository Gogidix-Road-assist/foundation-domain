package com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ObjectDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for ObjectDetection
 */
@Repository
public interface ObjectDetectionMongoRepository extends MongoRepository<ObjectDetectionEntity, String> {

    // All queries MUST include tenant filtering for multi-tenancy
    Optional<ObjectDetectionEntity> findByTenantIdAndUuid(String tenantId, UUID uuid);

    List<ObjectDetectionEntity> findByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    List<ObjectDetectionEntity> findByTenantId(String tenantId);

    List<ObjectDetectionEntity> findByTenantIdAndType(String tenantId, String type);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    long countByTenantIdAndImageAnalysisId(String tenantId, UUID imageAnalysisId);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);
}
