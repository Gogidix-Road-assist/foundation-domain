package com.gogidix.rapidassist.ai.modelmanagement.domain.repository;

import com.gogidix.rapidassist.ai.modelmanagement.domain.model.ModelPerformance;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ModelPerformance domain operations.
 */
public interface ModelPerformanceRepositoryPort {

    /**
     * Save a performance metric.
     */
    ModelPerformance save(String tenantId, ModelPerformance performance);

    /**
     * Find performance metric by ID.
     */
    Optional<ModelPerformance> findById(String tenantId, UUID id);

    /**
     * Find all metrics for a model.
     */
    List<ModelPerformance> findByModelId(String tenantId, UUID modelId);

    /**
     * Find metrics for a model version.
     */
    List<ModelPerformance> findByModelVersionId(String tenantId, UUID modelVersionId);

    /**
     * Find metrics for a deployment.
     */
    List<ModelPerformance> findByDeploymentId(String tenantId, UUID deploymentId);

    /**
     * Find metrics by evaluation type.
     */
    List<ModelPerformance> findByEvaluationType(String tenantId, UUID modelId, String evaluationType);

    /**
     * Find metrics within time range.
     */
    List<ModelPerformance> findByTimeRange(String tenantId, UUID modelId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * Find latest metric for a model.
     */
    Optional<ModelPerformance> findLatest(String tenantId, UUID modelId);

    /**
     * Check if metric exists.
     */
    boolean exists(String tenantId, UUID id);

    /**
     * Delete metrics by model version.
     */
    void deleteByModelVersionId(String tenantId, UUID modelVersionId);
}
