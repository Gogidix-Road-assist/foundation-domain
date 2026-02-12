package com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.modelmanagement.infrastructure.persistence.entity.ModelPerformanceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ModelPerformanceEntity.
 */
@Repository
public interface SpringDataModelPerformanceRepository extends MongoRepository<ModelPerformanceEntity, String> {

    /**
     * Find performance metric by UUID and tenant.
     */
    Optional<ModelPerformanceEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all metrics for a model.
     */
    List<ModelPerformanceEntity> findByModelIdAndTenantIdOrderByTimestampDesc(UUID modelId, String tenantId);

    /**
     * Find metrics for a model version.
     */
    List<ModelPerformanceEntity> findByModelVersionIdAndTenantIdOrderByTimestampDesc(UUID modelVersionId, String tenantId);

    /**
     * Find metrics for a deployment.
     */
    List<ModelPerformanceEntity> findByDeploymentIdAndTenantIdOrderByTimestampDesc(UUID deploymentId, String tenantId);

    /**
     * Find metrics by evaluation type.
     */
    List<ModelPerformanceEntity> findByModelIdAndTenantIdAndEvaluationTypeOrderByTimestampDesc(
            UUID modelId, String tenantId, String evaluationType);

    /**
     * Find metrics within time range.
     */
    List<ModelPerformanceEntity> findByModelIdAndTenantIdAndTimestampBetweenOrderByTimestampDesc(
            UUID modelId, String tenantId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find latest metric for a model.
     */
    Optional<ModelPerformanceEntity> findFirstByModelIdAndTenantIdOrderByTimestampDesc(UUID modelId, String tenantId);

    /**
     * Check if metric exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete metrics by model version.
     */
    void deleteByModelVersionIdAndTenantId(UUID modelVersionId, String tenantId);
}
