package com.gogidix.rapidassist.ai.forecasting.domain.repository;

import com.gogidix.rapidassist.ai.forecasting.domain.model.TimeSeriesData;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TimeSeriesData aggregate.
 * Defines the contract for time series data persistence operations.
 */
public interface TimeSeriesDataRepositoryPort {

    /**
     * Save time series data.
     *
     * @param tenantId The tenant ID
     * @param timeSeriesData The time series data to save
     * @return The saved time series data
     */
    TimeSeriesData save(String tenantId, TimeSeriesData timeSeriesData);

    /**
     * Find time series data by ID.
     *
     * @param tenantId The tenant ID
     * @param dataId The data ID
     * @return Optional containing the time series data if found
     */
    Optional<TimeSeriesData> findById(String tenantId, UUID dataId);

    /**
     * Find all time series data for a tenant.
     *
     * @param tenantId The tenant ID
     * @return List of time series data
     */
    List<TimeSeriesData> findByTenantId(String tenantId);

    /**
     * Find time series data by data source name.
     *
     * @param tenantId The tenant ID
     * @param dataSourceName The data source name
     * @return List of time series data
     */
    List<TimeSeriesData> findByDataSourceName(String tenantId, String dataSourceName);

    /**
     * Find time series data by granularity.
     *
     * @param tenantId The tenant ID
     * @param granularity The data granularity
     * @return List of time series data
     */
    List<TimeSeriesData> findByGranularity(String tenantId, com.gogidix.rapidassist.ai.forecasting.domain.model.DataGranularity granularity);

    /**
     * Find time series data with seasonality.
     *
     * @param tenantId The tenant ID
     * @return List of time series data with seasonality
     */
    List<TimeSeriesData> findWithSeasonality(String tenantId);

    /**
     * Find time series data with trend.
     *
     * @param tenantId The tenant ID
     * @return List of time series data with trend
     */
    List<TimeSeriesData> findWithTrend(String tenantId);

    /**
     * Delete time series data by ID.
     *
     * @param tenantId The tenant ID
     * @param dataId The data ID
     */
    void delete(String tenantId, UUID dataId);

    /**
     * Check if time series data exists.
     *
     * @param tenantId The tenant ID
     * @param dataId The data ID
     * @return true if the time series data exists
     */
    boolean exists(String tenantId, UUID dataId);

    /**
     * Count time series data by tenant.
     *
     * @param tenantId The tenant ID
     * @return The count of time series data
     */
    long countByTenantId(String tenantId);
}
