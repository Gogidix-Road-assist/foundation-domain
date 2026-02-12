package com.gogidix.rapidassist.ai.predictive.analytics.domain.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictiveModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for PredictiveModel aggregate.
 * Defines the contract for model persistence operations.
 */
public interface PredictiveModelRepositoryPort {

    /**
     * Save a predictive model
     */
    PredictiveModel save(PredictiveModel model);

    /**
     * Find model by ID and tenant
     */
    Optional<PredictiveModel> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find model by name and tenant
     */
    Optional<PredictiveModel> findByNameAndTenantId(String name, String tenantId);

    /**
     * Find all models for a tenant
     */
    List<PredictiveModel> findByTenantId(String tenantId);

    /**
     * Find models by status and tenant
     */
    List<PredictiveModel> findByStatusAndTenantId(ModelStatus status, String tenantId);

    /**
     * Find models by type and tenant
     */
    List<PredictiveModel> findByModelTypeAndTenantId(ModelType modelType, String tenantId);

    /**
     * Find active models for a tenant
     */
    List<PredictiveModel> findActiveModelsByTenantId(String tenantId);

    /**
     * Delete model by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Check if model exists by ID and tenant
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count models by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Find models that need retraining
     */
    List<PredictiveModel> findModelsNeedingRetraining(String tenantId, int thresholdDays);
}
