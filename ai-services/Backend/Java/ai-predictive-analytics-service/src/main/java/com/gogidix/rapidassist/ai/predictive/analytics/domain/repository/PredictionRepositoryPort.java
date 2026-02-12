package com.gogidix.rapidassist.ai.predictive.analytics.domain.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Prediction;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Prediction entity.
 * Defines the contract for prediction persistence operations.
 */
public interface PredictionRepositoryPort {

    /**
     * Save a prediction
     */
    Prediction save(Prediction prediction);

    /**
     * Find prediction by ID and tenant
     */
    Optional<Prediction> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find predictions by model ID and tenant
     */
    List<Prediction> findByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Find predictions by status and tenant
     */
    List<Prediction> findByStatusAndTenantId(PredictionStatus status, String tenantId);

    /**
     * Find predictions by date range and tenant
     */
    List<Prediction> findByCreatedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find recent predictions for a tenant
     */
    List<Prediction> findRecentPredictionsByTenantId(String tenantId, int limit);

    /**
     * Find pending or processing predictions
     */
    List<Prediction> findPendingPredictionsByTenantId(String tenantId);

    /**
     * Delete prediction by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count predictions by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count predictions by model and tenant
     */
    long countByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Delete old predictions
     */
    void deleteOldPredictions(LocalDateTime cutoffDate);
}
