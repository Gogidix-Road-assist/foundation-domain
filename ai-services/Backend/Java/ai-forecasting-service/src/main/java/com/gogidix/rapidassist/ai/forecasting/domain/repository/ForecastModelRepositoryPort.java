package com.gogidix.rapidassist.ai.forecasting.domain.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ForecastModel aggregate.
 * Defines the contract for forecast model persistence operations.
 */
public interface ForecastModelRepositoryPort {

    /**
     * Save a forecast model.
     *
     * @param tenantId The tenant ID
     * @param model The model to save
     * @return The saved model
     */
    ForecastModel save(String tenantId, ForecastModel model);

    /**
     * Find a model by ID.
     *
     * @param tenantId The tenant ID
     * @param modelId The model ID
     * @return Optional containing the model if found
     */
    Optional<ForecastModel> findById(String tenantId, UUID modelId);

    /**
     * Find all models for a tenant.
     *
     * @param tenantId The tenant ID
     * @return List of models
     */
    List<ForecastModel> findByTenantId(String tenantId);

    /**
     * Find models by type.
     *
     * @param tenantId The tenant ID
     * @param modelType The model type
     * @return List of models
     */
    List<ForecastModel> findByModelType(String tenantId, com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastModelType modelType);

    /**
     * Find models by status.
     *
     * @param tenantId The tenant ID
     * @param status The model status
     * @return List of models
     */
    List<ForecastModel> findByStatus(String tenantId, com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus status);

    /**
     * Find deployed models.
     *
     * @param tenantId The tenant ID
     * @return List of deployed models
     */
    List<ForecastModel> findDeployedModels(String tenantId);

    /**
     * Find trained models.
     *
     * @param tenantId The tenant ID
     * @return List of trained models
     */
    List<ForecastModel> findTrainedModels(String tenantId);

    /**
     * Update model status.
     *
     * @param tenantId The tenant ID
     * @param modelId The model ID
     * @param status The new status
     */
    void updateStatus(String tenantId, UUID modelId, com.gogidix.rapidassist.ai.forecasting.domain.model.ModelStatus status);

    /**
     * Delete a model by ID.
     *
     * @param tenantId The tenant ID
     * @param modelId The model ID
     */
    void delete(String tenantId, UUID modelId);

    /**
     * Check if a model exists.
     *
     * @param tenantId The tenant ID
     * @param modelId The model ID
     * @return true if the model exists
     */
    boolean exists(String tenantId, UUID modelId);

    /**
     * Count models by tenant.
     *
     * @param tenantId The tenant ID
     * @return The count of models
     */
    long countByTenantId(String tenantId);
}
