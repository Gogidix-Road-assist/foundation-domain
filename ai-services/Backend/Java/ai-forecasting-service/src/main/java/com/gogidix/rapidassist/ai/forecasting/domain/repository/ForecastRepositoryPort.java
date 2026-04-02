package com.gogidix.rapidassist.ai.forecasting.domain.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.Forecast;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Forecast aggregate.
 * Defines the contract for forecast persistence operations.
 */
public interface ForecastRepositoryPort {

    /**
     * Save a forecast.
     *
     * @param tenantId The tenant ID
     * @param forecast The forecast to save
     * @return The saved forecast
     */
    Forecast save(String tenantId, Forecast forecast);

    /**
     * Find a forecast by ID.
     *
     * @param tenantId The tenant ID
     * @param forecastId The forecast ID
     * @return Optional containing the forecast if found
     */
    Optional<Forecast> findById(String tenantId, UUID forecastId);

    /**
     * Find all forecasts for a tenant.
     *
     * @param tenantId The tenant ID
     * @return List of forecasts
     */
    List<Forecast> findByTenantId(String tenantId);

    /**
     * Find forecasts by model ID.
     *
     * @param tenantId The tenant ID
     * @param modelId The model ID
     * @return List of forecasts
     */
    List<Forecast> findByModelId(String tenantId, UUID modelId);

    /**
     * Find forecasts by status.
     *
     * @param tenantId The tenant ID
     * @param status The forecast status
     * @return List of forecasts
     */
    List<Forecast> findByStatus(String tenantId, com.gogidix.rapidassist.ai.forecasting.domain.model.ForecastStatus status);

    /**
     * Find forecasts by date range.
     *
     * @param tenantId The tenant ID
     * @param startDate The start date
     * @param endDate The end date
     * @return List of forecasts
     */
    List<Forecast> findByDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find completed forecasts.
     *
     * @param tenantId The tenant ID
     * @return List of completed forecasts
     */
    List<Forecast> findCompletedForecasts(String tenantId);

    /**
     * Find failed forecasts.
     *
     * @param tenantId The tenant ID
     * @return List of failed forecasts
     */
    List<Forecast> findFailedForecasts(String tenantId);

    /**
     * Delete a forecast by ID.
     *
     * @param tenantId The tenant ID
     * @param forecastId The forecast ID
     */
    void delete(String tenantId, UUID forecastId);

    /**
     * Check if a forecast exists.
     *
     * @param tenantId The tenant ID
     * @param forecastId The forecast ID
     * @return true if the forecast exists
     */
    boolean exists(String tenantId, UUID forecastId);

    /**
     * Count forecasts by tenant.
     *
     * @param tenantId The tenant ID
     * @return The count of forecasts
     */
    long countByTenantId(String tenantId);
}
