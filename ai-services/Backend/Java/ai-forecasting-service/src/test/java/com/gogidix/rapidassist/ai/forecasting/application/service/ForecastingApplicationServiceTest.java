package com.gogidix.rapidassist.ai.forecasting.application.service;

import com.gogidix.rapidassist.ai.forecasting.application.command.GenerateForecastCommand;
import com.gogidix.rapidassist.ai.forecasting.domain.model.*;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.ForecastRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.ForecastModelRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.TimeSeriesDataRepositoryPort;
import com.gogidix.rapidassist.ai.forecasting.application.mapper.ForecastMapper;
import com.gogidix.rapidassist.ai.forecasting.application.mapper.ForecastModelMapper;
import com.gogidix.rapidassist.ai.forecasting.application.mapper.TimeSeriesDataMapper;
import com.gogidix.rapidassist.ai.forecasting.application.mapper.ForecastConfigurationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ForecastingApplicationService.
 */
@ExtendWith(MockitoExtension.class)
class ForecastingApplicationServiceTest {

    @Mock
    private ForecastRepositoryPort forecastRepository;

    @Mock
    private ForecastModelRepositoryPort modelRepository;

    @Mock
    private TimeSeriesDataRepositoryPort timeSeriesDataRepository;

    @Mock
    private ForecastMapper forecastMapper;

    @Mock
    private ForecastModelMapper modelMapper;

    @Mock
    private TimeSeriesDataMapper timeSeriesDataMapper;

    @Mock
    private ForecastConfigurationMapper configurationMapper;

    private ForecastingApplicationService applicationService;

    @BeforeEach
    void setUp() {
        applicationService = new ForecastingApplicationService(
                forecastRepository,
                modelRepository,
                timeSeriesDataRepository,
                forecastMapper,
                modelMapper,
                timeSeriesDataMapper,
                configurationMapper
        );
    }

    @Test
    void testGenerateForecast_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID modelId = UUID.randomUUID();
        Map<String, Object> parameters = new HashMap<>();

        GenerateForecastCommand command = GenerateForecastCommand.builder()
                .tenantId(tenantId)
                .forecastName("Test Forecast")
                .description("Test description")
                .modelId(modelId)
                .forecastHorizon(12)
                .granularity(DataGranularity.MONTHLY)
                .parameters(parameters)
                .createdBy("test-user")
                .build();

        Forecast savedForecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .modelId(modelId)
                .forecastName("Test Forecast")
                .status(ForecastStatus.COMPLETED)
                .build();

        when(forecastRepository.save(anyString(), any(Forecast.class)))
                .thenReturn(savedForecast);
        when(forecastMapper.toDto(any(Forecast.class)))
                .thenReturn(null);

        // When
        applicationService.generateForecast(command);

        // Then
        verify(forecastRepository, times(2)).save(eq(tenantId), any(Forecast.class));
    }

    @Test
    void testGenerateForecast_WithNullModelId_ThrowsException() {
        // Given
        GenerateForecastCommand command = GenerateForecastCommand.builder()
                .tenantId("tenant-123")
                .modelId(null)
                .build();

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.generateForecast(command);
        });
    }

    @Test
    void testDeleteForecast_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        when(forecastRepository.exists(tenantId, forecastId)).thenReturn(true);
        doNothing().when(forecastRepository).delete(tenantId, forecastId);

        // When
        applicationService.deleteForecast(tenantId, forecastId);

        // Then
        verify(forecastRepository).delete(tenantId, forecastId);
    }

    @Test
    void testDeleteForecast_NotFound_ThrowsException() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        when(forecastRepository.exists(tenantId, forecastId)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.deleteForecast(tenantId, forecastId);
        });
    }

    @Test
    void testCreateModel_Success() {
        // Given
        String tenantId = "tenant-123";
        Map<String, Object> hyperparameters = new HashMap<>();
        hyperparameters.put("epochs", 100);
        hyperparameters.put("learning_rate", 0.01);

        Map<String, Object> trainingParameters = new HashMap<>();
        trainingParameters.put("batch_size", 32);

        com.gogidix.rapidassist.ai.forecasting.application.command.CreateForecastModelCommand command =
            com.gogidix.rapidassist.ai.forecasting.application.command.CreateForecastModelCommand.builder()
                .tenantId(tenantId)
                .modelName("Sales Forecast Model")
                .modelType(ForecastModelType.ARIMA)
                .description("ARIMA model for sales forecasting")
                .hyperparameters(hyperparameters)
                .trainingParameters(trainingParameters)
                .createdBy("admin")
                .build();

        ForecastModel savedModel = ForecastModel.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .modelName("Sales Forecast Model")
            .modelType(ForecastModelType.ARIMA)
            .status(ModelStatus.TRAINED)
            .build();

        when(modelRepository.save(anyString(), any(ForecastModel.class))).thenReturn(savedModel);
        when(modelMapper.toDto(any(ForecastModel.class))).thenReturn(null);

        // When
        applicationService.createModel(command);

        // Then
        verify(modelRepository).save(eq(tenantId), any(ForecastModel.class));
    }

    @Test
    void testGetForecast_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        Forecast forecast = Forecast.builder()
            .id(forecastId)
            .tenantId(tenantId)
            .forecastName("Test Forecast")
            .status(ForecastStatus.COMPLETED)
            .build();

        when(forecastRepository.findById(tenantId, forecastId)).thenReturn(Optional.of(forecast));
        when(forecastMapper.toDto(any(Forecast.class))).thenReturn(null);

        // When
        applicationService.getForecast(
            com.gogidix.rapidassist.ai.forecasting.application.query.GetForecastQuery.builder()
                .tenantId(tenantId)
                .forecastId(forecastId)
                .build()
        );

        // Then
        verify(forecastRepository).findById(tenantId, forecastId);
    }

    @Test
    void testGetForecast_NotFound_ThrowsException() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();

        when(forecastRepository.findById(tenantId, forecastId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.getForecast(
                com.gogidix.rapidassist.ai.forecasting.application.query.GetForecastQuery.builder()
                    .tenantId(tenantId)
                    .forecastId(forecastId)
                    .build()
            );
        });
    }

    @Test
    void testListForecasts_Success() {
        // Given
        String tenantId = "tenant-123";

        Forecast forecast1 = Forecast.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .forecastName("Forecast 1")
            .build();

        Forecast forecast2 = Forecast.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .forecastName("Forecast 2")
            .build();

        when(forecastRepository.findByTenantId(tenantId)).thenReturn(java.util.List.of(forecast1, forecast2));
        when(forecastMapper.toDto(any())).thenReturn(null);

        // When
        applicationService.listForecasts(
            com.gogidix.rapidassist.ai.forecasting.application.query.ListForecastsQuery.builder()
                .tenantId(tenantId)
                .build()
        );

        // Then
        verify(forecastRepository).findByTenantId(tenantId);
    }

    @Test
    void testSubmitTimeSeriesData_Success() {
        // Given
        String tenantId = "tenant-123";

        java.util.List<TimeSeriesData.DataPoint> dataPoints = java.util.List.of(
            TimeSeriesData.DataPoint.builder()
                .timestamp(LocalDateTime.now())
                .value(new BigDecimal("100.50"))
                .build()
        );

        com.gogidix.rapidassist.ai.forecasting.application.command.UpdateTimeSeriesDataCommand command =
            com.gogidix.rapidassist.ai.forecasting.application.command.UpdateTimeSeriesDataCommand.builder()
                .tenantId(tenantId)
                .dataSourceName("Sales Data")
                .description("Monthly sales data")
                .granularity(DataGranularity.MONTHLY)
                .dataPoints(dataPoints)
                .frequencyType(FrequencyType.CONSTANT)
                .hasSeasonality(true)
                .hasTrend(true)
                .seasonalityPeriod(12)
                .createdBy("admin")
                .build();

        TimeSeriesData savedData = TimeSeriesData.builder()
            .id(UUID.randomUUID())
            .tenantId(tenantId)
            .dataSourceName("Sales Data")
            .build();

        when(timeSeriesDataRepository.save(anyString(), any(TimeSeriesData.class))).thenReturn(savedData);
        when(timeSeriesDataMapper.toDto(any(TimeSeriesData.class))).thenReturn(null);

        // When
        applicationService.submitTimeSeriesData(command);

        // Then
        verify(timeSeriesDataRepository).save(eq(tenantId), any(TimeSeriesData.class));
    }

    @Test
    void testCreateConfiguration_Success() {
        // Given
        String tenantId = "tenant-123";
        Map<String, Object> modelParameters = new HashMap<>();
        modelParameters.put("p", 1);
        modelParameters.put("d", 1);
        modelParameters.put("q", 1);

        com.gogidix.rapidassist.ai.forecasting.application.command.CreateForecastConfigurationCommand command =
            com.gogidix.rapidassist.ai.forecasting.application.command.CreateForecastConfigurationCommand.builder()
                .tenantId(tenantId)
                .configName("Default Config")
                .modelType(ForecastModelType.ARIMA)
                .forecastHorizon(12)
                .granularity(DataGranularity.MONTHLY)
                .confidenceLevel(new BigDecimal("0.95"))
                .enableSeasonality(true)
                .seasonalityPeriod(12)
                .enableTrend(true)
                .modelParameters(modelParameters)
                .maxHistoryDataPoints(1000)
                .minHistoryDataPoints(50)
                .enableOutlierDetection(true)
                .enableAnomalyDetection(true)
                .isActive(true)
                .createdBy("admin")
                .build();

        when(configurationMapper.toDto(any())).thenReturn(null);

        // When
        applicationService.createConfiguration(command);

        // Then
        // Configuration created successfully (no repository in current implementation)
    }

    @Test
    void testGetForecastAccuracy_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID forecastId = UUID.randomUUID();
        UUID modelId = UUID.randomUUID();

        Forecast forecast = Forecast.builder()
            .id(forecastId)
            .tenantId(tenantId)
            .modelId(modelId)
            .forecastName("Test Forecast")
            .status(ForecastStatus.COMPLETED)
            .build();

        when(forecastRepository.findById(tenantId, forecastId)).thenReturn(Optional.of(forecast));

        // When
        applicationService.getForecastAccuracy(
            com.gogidix.rapidassist.ai.forecasting.application.query.GetForecastAccuracyQuery.builder()
                .tenantId(tenantId)
                .forecastId(forecastId)
                .build()
        );

        // Then
        verify(forecastRepository).findById(tenantId, forecastId);
    }

    @Test
    void testUpdateModel_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID modelId = UUID.randomUUID();

        com.gogidix.rapidassist.ai.forecasting.application.command.UpdateForecastModelCommand command =
            com.gogidix.rapidassist.ai.forecasting.application.command.UpdateForecastModelCommand.builder()
                .tenantId(tenantId)
                .modelId(modelId)
                .modelName("Updated Model")
                .description("Updated description")
                .updatedBy("admin")
                .build();

        ForecastModel existingModel = ForecastModel.builder()
            .id(modelId)
            .tenantId(tenantId)
            .modelName("Original Model")
            .status(ModelStatus.TRAINED)
            .build();

        when(modelRepository.findById(tenantId, modelId)).thenReturn(Optional.of(existingModel));
        when(modelRepository.save(anyString(), any(ForecastModel.class))).thenReturn(existingModel);
        when(modelMapper.toDto(any(ForecastModel.class))).thenReturn(null);

        // When
        applicationService.updateModel(command);

        // Then
        verify(modelRepository).save(eq(tenantId), any(ForecastModel.class));
    }

    @Test
    void testUpdateModel_NotFound_ThrowsException() {
        // Given
        String tenantId = "tenant-123";
        UUID modelId = UUID.randomUUID();

        com.gogidix.rapidassist.ai.forecasting.application.command.UpdateForecastModelCommand command =
            com.gogidix.rapidassist.ai.forecasting.application.command.UpdateForecastModelCommand.builder()
                .tenantId(tenantId)
                .modelId(modelId)
                .build();

        when(modelRepository.findById(tenantId, modelId)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.updateModel(command);
        });
    }

    @Test
    void testDeleteModel_Success() {
        // Given
        String tenantId = "tenant-123";
        UUID modelId = UUID.randomUUID();

        when(modelRepository.exists(tenantId, modelId)).thenReturn(true);
        doNothing().when(modelRepository).delete(tenantId, modelId);

        // When
        applicationService.deleteModel(tenantId, modelId);

        // Then
        verify(modelRepository).delete(tenantId, modelId);
    }

    @Test
    void testDeleteModel_NotFound_ThrowsException() {
        // Given
        String tenantId = "tenant-123";
        UUID modelId = UUID.randomUUID();

        when(modelRepository.exists(tenantId, modelId)).thenReturn(false);

        // When/Then
        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.deleteModel(tenantId, modelId);
        });
    }
}
