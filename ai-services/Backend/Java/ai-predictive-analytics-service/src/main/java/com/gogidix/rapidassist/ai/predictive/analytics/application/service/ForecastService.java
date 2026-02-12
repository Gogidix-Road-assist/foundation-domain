package com.gogidix.rapidassist.ai.predictive.analytics.application.service;

import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Forecast;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.ForecastRepositoryPort;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.repository.PredictiveModelRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for managing forecasts.
 * Handles time series forecasting operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ForecastService {

    private final ForecastRepositoryPort forecastRepository;
    private final PredictiveModelRepositoryPort modelRepository;

    /**
     * Create a new forecast
     */
    @Transactional
    public Forecast createForecast(String tenantId, UUID modelId, String forecastName,
                                   LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Creating forecast: {} for model: {} in tenant: {}", forecastName, modelId, tenantId);

        // Verify model exists
        modelRepository.findByIdAndTenantId(modelId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Model not found with ID: " + modelId));

        Forecast forecast = Forecast.initialize(
                tenantId,
                modelId,
                forecastName,
                startDate,
                endDate
        );

        Forecast savedForecast = forecastRepository.save(forecast);
        log.info("Created forecast with ID: {}", savedForecast.getId());

        return savedForecast;
    }

    /**
     * Generate forecast (simulate forecast generation)
     */
    @Transactional
    public Forecast generateForecast(UUID forecastId, String tenantId) {
        log.info("Generating forecast: {} for tenant: {}", forecastId, tenantId);

        Forecast forecast = forecastRepository.findByIdAndTenantId(forecastId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Forecast not found with ID: " + forecastId));

        if (forecast.getStatus() == ModelStatus.ACTIVE) {
            throw new IllegalStateException("Forecast has already been generated");
        }

        // Simulate forecast generation
        try {
            // Simulate processing time
            Thread.sleep(200);

            // Generate simulated forecast data
            List<Forecast.ForecastDataPoint> forecastData = generateSimulatedForecastData(
                    forecast.getForecastStartDate(),
                    forecast.getForecastEndDate()
            );

            forecast.setForecastData(forecastData);
            forecast.setStatus(ModelStatus.ACTIVE);
            forecast.setGeneratedAt(LocalDateTime.now());

            // Set simulated accuracy metrics
            forecast.setMeanAbsoluteError(2.5);
            forecast.setMeanAbsolutePercentageError(5.2);

            log.info("Forecast generated successfully: {}", forecastId);

        } catch (Exception e) {
            forecast.setStatus(ModelStatus.FAILED);
            log.error("Forecast generation failed: {}", forecastId, e);
        }

        return forecastRepository.save(forecast);
    }

    /**
     * Get forecast by ID
     */
    public Forecast getForecastById(UUID forecastId, String tenantId) {
        return forecastRepository.findByIdAndTenantId(forecastId, tenantId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Forecast not found with ID: " + forecastId));
    }

    /**
     * Get forecasts by model
     */
    public List<Forecast> getForecastsByModel(UUID modelId, String tenantId) {
        return forecastRepository.findByModelIdAndTenantId(modelId, tenantId);
    }

    /**
     * Get recent forecasts for a tenant
     */
    public List<Forecast> getRecentForecasts(String tenantId, int limit) {
        return forecastRepository.findRecentForecastsByTenantId(tenantId, limit);
    }

    /**
     * Get forecasts by date range
     */
    public List<Forecast> getForecastsByDateRange(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        return forecastRepository.findByGeneratedAtBetweenAndTenantId(startDate, endDate, tenantId);
    }

    /**
     * Get forecasts within forecast date range
     */
    public List<Forecast> getForecastsByForecastDateRange(LocalDateTime startDate, LocalDateTime endDate, String tenantId) {
        return forecastRepository.findByForecastStartDateBetweenAndTenantId(startDate, endDate, tenantId);
    }

    /**
     * Delete old forecasts
     */
    @Transactional
    public void deleteOldForecasts(LocalDateTime cutoffDate) {
        log.info("Deleting old forecasts generated before: {}", cutoffDate);
        forecastRepository.deleteOldForecasts(cutoffDate);
    }

    /**
     * Delete a forecast
     */
    @Transactional
    public void deleteForecast(UUID forecastId, String tenantId) {
        log.info("Deleting forecast with ID: {} for tenant: {}", forecastId, tenantId);
        forecastRepository.deleteByIdAndTenantId(forecastId, tenantId);
    }

    /**
     * Generate simulated forecast data
     */
    private List<Forecast.ForecastDataPoint> generateSimulatedForecastData(LocalDateTime startDate, LocalDateTime endDate) {
        List<Forecast.ForecastDataPoint> dataPoints = new java.util.ArrayList<>();

        LocalDateTime current = startDate;
        double value = 100.0;

        while (!current.isAfter(endDate)) {
            // Simulate forecast with trend and random variation
            value += (Math.random() - 0.4) * 5; // Slight upward trend with variation
            value = Math.max(value, 0); // Ensure non-negative

            Forecast.ForecastDataPoint dataPoint = Forecast.ForecastDataPoint.builder()
                    .timestamp(current)
                    .value(value)
                    .confidence(0.85 + (Math.random() * 0.1)) // Random confidence between 0.85 and 0.95
                    .metadata(java.util.Map.of("source", "simulated"))
                    .build();

            dataPoints.add(dataPoint);
            current = current.plusDays(1); // Daily forecast

            // Limit to 365 data points
            if (dataPoints.size() >= 365) {
                break;
            }
        }

        return dataPoints;
    }
}
