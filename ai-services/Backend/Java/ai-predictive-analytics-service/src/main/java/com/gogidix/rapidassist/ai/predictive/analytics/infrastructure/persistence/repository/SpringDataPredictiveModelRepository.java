package com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import com.gogidix.rapidassist.ai.predictive.analytics.infrastructure.persistence.entity.PredictiveModelEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for PredictiveModelEntity.
 */
@Repository
public interface SpringDataPredictiveModelRepository extends MongoRepository<PredictiveModelEntity, String> {

    /**
     * Find model by UUID and tenant.
     */
    Optional<PredictiveModelEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find model by name and tenant.
     */
    Optional<PredictiveModelEntity> findByNameAndTenantId(String name, String tenantId);

    /**
     * Find all models for a tenant.
     */
    List<PredictiveModelEntity> findByTenantId(String tenantId);

    /**
     * Find models by status and tenant.
     */
    List<PredictiveModelEntity> findByStatusAndTenantId(ModelStatus status, String tenantId);

    /**
     * Find models by type and tenant.
     */
    List<PredictiveModelEntity> findByModelTypeAndTenantId(ModelType modelType, String tenantId);

    /**
     * Find active models for a tenant.
     */
    List<PredictiveModelEntity> findByTenantIdAndStatus(String tenantId, ModelStatus status);

    /**
     * Find all active models for a tenant.
     */
    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE' }")
    List<PredictiveModelEntity> findActiveModelsByTenantId(String tenantId);

    /**
     * Check if model exists by UUID and tenant.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count models by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Find models needing retraining (last trained before threshold).
     */
    @Query("{ 'tenantId': ?0, 'lastTrainedAt': { $lt: ?1 } }")
    List<PredictiveModelEntity> findModelsNeedingRetraining(String tenantId, LocalDateTime threshold);

    /**
     * Delete model by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
