package com.gogidix.rapidassist.ai.forecasting.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Forecast domain model.
 */
class ForecastModelTest {

    @Test
    void testForecastCreation() {
        // Given
        UUID modelId = UUID.randomUUID();
        Map<String, Object> parameters = new HashMap<>();

        Forecast forecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .modelId(modelId)
                .forecastName("Sales Forecast")
                .description("Monthly sales forecast")
                .status(ForecastStatus.PENDING)
                .forecastHorizon(12)
                .granularity(DataGranularity.MONTHLY)
                .parameters(parameters)
                .createdAt(LocalDateTime.now())
                .createdBy("test-user")
                .build();

        // Then
        assertNotNull(forecast);
        assertEquals("Sales Forecast", forecast.getForecastName());
        assertEquals(ForecastStatus.PENDING, forecast.getStatus());
        assertEquals(DataGranularity.MONTHLY, forecast.getGranularity());
        assertEquals(12, forecast.getForecastHorizon());
        assertFalse(forecast.isCompleted());
        assertFalse(forecast.isFailed());
        assertFalse(forecast.isInProgress());
    }

    @Test
    void testForecastStatusTransitions() {
        // Given
        Forecast forecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .status(ForecastStatus.PENDING)
                .build();

        // When - status changes to GENERATING
        forecast.setStatus(ForecastStatus.GENERATING);

        // Then
        assertTrue(forecast.isInProgress());
        assertFalse(forecast.isCompleted());

        // When - status changes to COMPLETED
        forecast.setStatus(ForecastStatus.COMPLETED);

        // Then
        assertTrue(forecast.isCompleted());
        assertFalse(forecast.isInProgress());
    }

    @Test
    void testForecastDuration() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 0, 0);

        Forecast forecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .startDate(startDate)
                .endDate(endDate)
                .build();

        // When
        long duration = forecast.getForecastDurationDays();

        // Then
        assertEquals(30, duration);
    }

    @Test
    void testForecastWithDataPoints() {
        // Given
        Forecast.ForecastDataPoint dataPoint1 = Forecast.ForecastDataPoint.builder()
                .timestamp(LocalDateTime.now())
                .predictedValue(new BigDecimal("100.5"))
                .lowerBound(new BigDecimal("95.0"))
                .upperBound(new BigDecimal("106.0"))
                .build();

        Forecast forecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .dataPoints(java.util.List.of(dataPoint1))
                .build();

        // Then
        assertTrue(forecast.hasDataPoints());
        assertEquals(1, forecast.getDataPointsCount());
    }

    @Test
    void testForecastModelAccuracy() {
        // Given
        ForecastModel model = ForecastModel.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .modelName("ARIMA Model")
                .modelType(ForecastModelType.ARIMA)
                .status(ModelStatus.TRAINED)
                .testAccuracy(new BigDecimal("0.92"))
                .validationAccuracy(new BigDecimal("0.88"))
                .trainingAccuracy(new BigDecimal("0.90"))
                .build();

        // When
        boolean isTrained = model.isTrained();
        BigDecimal overallAccuracy = model.getOverallAccuracy();

        // Then
        assertTrue(isTrained);
        assertEquals(new BigDecimal("0.92"), overallAccuracy);
    }

    @Test
    void testTimeSeriesDataWithSeasonality() {
        // Given
        TimeSeriesData timeSeriesData = TimeSeriesData.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .dataSourceName("Monthly Sales")
                .granularity(DataGranularity.MONTHLY)
                .hasSeasonality(true)
                .hasTrend(true)
                .seasonalityPeriod(12)
                .build();

        // When
        boolean hasSeasonality = timeSeriesData.hasSeasonalityPattern();
        boolean hasTrend = timeSeriesData.hasTrendPattern();

        // Then
        assertTrue(hasSeasonality);
        assertTrue(hasTrend);
        assertEquals(12, timeSeriesData.getSeasonalityPeriod());
    }

    @Test
    void testForecastConfiguration() {
        // Given
        ForecastConfiguration config = ForecastConfiguration.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-123")
                .configName("Default Config")
                .modelType(ForecastModelType.LSTM)
                .forecastHorizon(24)
                .granularity(DataGranularity.HOURLY)
                .enableSeasonality(true)
                .enableTrend(true)
                .isActive(true)
                .build();

        // When
        boolean isValid = config.isValidConfiguration();
        boolean isActive = config.isActiveConfiguration();

        // Then
        assertTrue(isValid);
        assertTrue(isActive);
        assertTrue(config.isSeasonalityEnabled());
        assertTrue(config.isTrendEnabled());
    }
}
