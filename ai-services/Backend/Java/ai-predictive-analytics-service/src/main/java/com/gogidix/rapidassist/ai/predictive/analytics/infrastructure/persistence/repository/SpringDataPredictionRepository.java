package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.PredictionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PredictionEntity.
 */
@Repository
public interface SpringDataPredictionRepository extends MongoRepository<PredictionEntity, String> {

    /**
     * Find prediction by UUID and tenant.
     */
    Optional<PredictionEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find predictions by model ID and tenant.
     */
    List<PredictionEntity> findByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Find predictions by status and tenant.
     */
    List<PredictionEntity> findByStatusAndTenantId(PredictionStatus status, String tenantId);

    /**
     * Find predictions by date range and tenant.
     */
    List<PredictionEntity> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find recent predictions for a tenant.
     */
    @Query("{ 'tenantId': ?0 }")
    List<PredictionEntity> findByTenantId(String tenantId);

    /**
     * Find pending or processing predictions.
     */
    @Query("{ 'tenantId': ?0, 'status': { $in: ['PENDING', 'PROCESSING'] } }")
    List<PredictionEntity> findPendingPredictionsByTenantId(String tenantId);

    /**
     * Count predictions by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count predictions by model and tenant.
     */
    long countByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Delete old predictions.
     */
    @Query("{ 'completedAt': { $lt: ?0 } }")
    void deleteByCompletedAtBefore(LocalDateTime cutoffDate);

    /**
     * Delete prediction by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
