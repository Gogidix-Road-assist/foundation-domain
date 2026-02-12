package com.gogidix.rapidassist.ai.predictive.analytics.domain.repository;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Forecast;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Forecast entity.
 * Defines the contract for forecast persistence operations.
 */
public interface ForecastRepositoryPort {

    /**
     * Save a forecast
     */
    Forecast save(Forecast forecast);

    /**
     * Find forecast by ID and tenant
     */
    Optional<Forecast> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find forecasts by model ID and tenant
     */
    List<Forecast> findByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Find forecasts by date range and tenant
     */
    List<Forecast> findByGeneratedAtBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Find recent forecasts for a tenant
     */
    List<Forecast> findRecentForecastsByTenantId(String tenantId, int limit);

    /**
     * Find forecasts within a forecast date range
     */
    List<Forecast> findByForecastStartDateBetweenAndTenantId(LocalDateTime startDate, LocalDateTime endDate, String tenantId);

    /**
     * Delete forecast by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count forecasts by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count forecasts by model and tenant
     */
    long countByModelIdAndTenantId(UUID modelId, String tenantId);

    /**
     * Delete old forecasts
     */
    void deleteOldForecasts(LocalDateTime cutoffDate);
}
