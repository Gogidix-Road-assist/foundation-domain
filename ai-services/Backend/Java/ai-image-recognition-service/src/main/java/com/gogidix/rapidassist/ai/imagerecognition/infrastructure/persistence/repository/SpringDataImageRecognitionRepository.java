package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.persistence.entity.ImageRecognitionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ImageRecognitionEntity.
 */
@Repository
public interface SpringDataImageRecognitionRepository extends MongoRepository<ImageRecognitionEntity, String> {

    /**
     * Find by UUID and tenant.
     */
    Optional<ImageRecognitionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find by request ID and tenant.
     */
    Optional<ImageRecognitionEntity> findByRequestIdAndTenantId(String requestId, String tenantId);

    /**
     * Find all by tenant.
     */
    List<ImageRecognitionEntity> findByTenantId(String tenantId);

    /**
     * Find by user ID and tenant.
     */
    List<ImageRecognitionEntity> findByUserIdAndTenantId(String userId, String tenantId);

    /**
     * Find by status and tenant.
     */
    List<ImageRecognitionEntity> findByStatusAndTenantId(RecognitionStatus status, String tenantId);

    /**
     * Check if exists by UUID and tenant.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count by tenant.
     */
    long countByTenantId(String tenantId);
}
