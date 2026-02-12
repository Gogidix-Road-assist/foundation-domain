package com.gogidix.rapidassist.ai.forecasting.application.service;

import com.gogidix.rapidassist.ai.forecasting.application.command.*;
import com.gogidix.rapidassist.ai.forecasting.application.dto.*;
import com.gogidix.rapidassist.ai.forecasting.application.mapper.*;
import com.gogidix.rapidassist.ai.forecasting.application.query.*;
import com.gogidix.rapidassist.ai.forecasting.domain.model.*;
import com.gogidix.rapidassist.ai.forecasting.domain.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for Forecasting operations.
 * Orchestrates business logic and coordinates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastingApplicationService {

    private final ForecastRepositoryPort forecastRepository;
    private final ForecastModelRepositoryPort modelRepository;
    private final TimeSeriesDataRepositoryPort timeSeriesDataRepository;
    private final ForecastMapper forecastMapper;
    private final ForecastModelMapper modelMapper;
    private final TimeSeriesDataMapper timeSeriesDataMapper;
    private final ForecastConfigurationMapper configurationMapper;

    // ==================== Forecast Operations ====================

    /**
     * Generate a new forecast.
     */
    @Transactional
    public ForecastDto generateForecast(GenerateForecastCommand command) {
        log.info("Generating forecast: {} for tenant: {}", command.getForecastName(), command.getTenantId());

        // Validate model exists
        UUID modelId = command.getModelId();
        if (modelId == null) {
            throw new IllegalArgumentException("Model ID is required");
        }

        // Create forecast with pending status
        Forecast forecast = Forecast.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .modelId(modelId)
                .forecastName(command.getForecastName())
                .description(command.getDescription())
                .status(ForecastStatus.PENDING)
                .startDate(command.getStartDate())
                .endDate(command.getEndDate())
                .forecastHorizon(command.getForecastHorizon())
                .granularity(command.getGranularity())
                .parameters(command.getParameters())
                .metadata(command.getMetadata())
                .createdAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .build();

        // Update status to generating
        forecast.setStatus(ForecastStatus.GENERATING);

        // Save forecast
        Forecast savedForecast = forecastRepository.save(command.getTenantId(), forecast);

        // Simulate forecast generation (in real implementation, this would call ML service)
        forecast.setStatus(ForecastStatus.COMPLETED);
        forecast.setUpdatedAt(LocalDateTime.now());
        Forecast finalForecast = forecastRepository.save(command.getTenantId(), forecast);

        log.info("Forecast generated successfully: {}", finalForecast.getId());
        return forecastMapper.toDto(finalForecast);
    }

    /**
     * Get a forecast by ID.
     */
    @Transactional(readOnly = true)
    public ForecastDto getForecast(GetForecastQuery query) {
        log.info("Getting forecast: {} for tenant: {}", query.getForecastId(), query.getTenantId());

        Forecast forecast = forecastRepository.findById(query.getTenantId(), query.getForecastId())
                .orElseThrow(() -> new IllegalArgumentException("Forecast not found: " + query.getForecastId()));

        return forecastMapper.toDto(forecast);
    }

    /**
     * List all forecasts with pagination.
     */
    @Transactional(readOnly = true)
    public List<ForecastDto> listForecasts(ListForecastsQuery query) {
        log.info("Listing forecasts for tenant: {}", query.getTenantId());

        List<Forecast> forecasts;

        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            ForecastStatus status = ForecastStatus.valueOf(query.getStatus().toUpperCase());
            forecasts = forecastRepository.findByStatus(query.getTenantId(), status);
        } else {
            forecasts = forecastRepository.findByTenantId(query.getTenantId());
        }

        // Apply pagination
        int page = query.getPage() != null ? query.getPage() : 0;
        int size = query.getSize() != null ? query.getSize() : 20;

        int start = page * size;
        int end = Math.min(start + size, forecasts.size());

        if (start >= forecasts.size()) {
            return List.of();
        }

        return forecasts.subList(start, end).stream()
                .map(forecastMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Delete a forecast.
     */
    @Transactional
    public void deleteForecast(String tenantId, UUID forecastId) {
        log.info("Deleting forecast: {} for tenant: {}", forecastId, tenantId);
        if (!forecastRepository.exists(tenantId, forecastId)) {
            throw new IllegalArgumentException("Forecast not found: " + forecastId);
        }
        forecastRepository.delete(tenantId, forecastId);
    }

    // ==================== Model Operations ====================

    /**
     * Create a new forecast model.
     */
    @Transactional
    public ForecastModelDto createModel(CreateForecastModelCommand command) {
        log.info("Creating model: {} for tenant: {}", command.getModelName(), command.getTenantId());

        ForecastModel model = ForecastModel.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .modelName(command.getModelName())
                .description(command.getDescription())
                .modelType(command.getModelType())
                .status(ModelStatus.TRAINING)
                .version(command.getVersion())
                .hyperparameters(command.getHyperparameters())
                .trainingParameters(command.getTrainingParameters())
                .metadata(command.getMetadata())
                .createdAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .build();

        // Simulate training completion
        model.setStatus(ModelStatus.TRAINED);
        model.setLastTrainedAt(LocalDateTime.now());
        model.setTrainingAccuracy(new BigDecimal("0.85"));
        model.setValidationAccuracy(new BigDecimal("0.82"));

        ForecastModel savedModel = modelRepository.save(command.getTenantId(), model);

        log.info("Model created successfully: {}", savedModel.getId());
        return modelMapper.toDto(savedModel);
    }

    /**
     * Update a forecast model.
     */
    @Transactional
    public ForecastModelDto updateModel(UpdateForecastModelCommand command) {
        log.info("Updating model: {} for tenant: {}", command.getModelId(), command.getTenantId());

        ForecastModel existingModel = modelRepository.findById(command.getTenantId(), command.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + command.getModelId()));

        // Update fields
        if (command.getModelName() != null) {
            existingModel.setModelName(command.getModelName());
        }
        if (command.getDescription() != null) {
            existingModel.setDescription(command.getDescription());
        }
        if (command.getStatus() != null) {
            existingModel.setStatus(command.getStatus());
        }
        if (command.getVersion() != null) {
            existingModel.setVersion(command.getVersion());
        }
        if (command.getHyperparameters() != null) {
            existingModel.setHyperparameters(command.getHyperparameters());
        }
        existingModel.setUpdatedAt(LocalDateTime.now());
        existingModel.setUpdatedBy(command.getUpdatedBy());

        ForecastModel updatedModel = modelRepository.save(command.getTenantId(), existingModel);

        log.info("Model updated successfully: {}", updatedModel.getId());
        return modelMapper.toDto(updatedModel);
    }

    /**
     * List all models.
     */
    @Transactional(readOnly = true)
    public List<ForecastModelDto> listModels(String tenantId) {
        log.info("Listing models for tenant: {}", tenantId);
        return modelRepository.findByTenantId(tenantId).stream()
                .map(modelMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get a model by ID.
     */
    @Transactional(readOnly = true)
    public ForecastModelDto getModel(String tenantId, UUID modelId) {
        log.info("Getting model: {} for tenant: {}", modelId, tenantId);
        ForecastModel model = modelRepository.findById(tenantId, modelId)
                .orElseThrow(() -> new IllegalArgumentException("Model not found: " + modelId));
        return modelMapper.toDto(model);
    }

    /**
     * Delete a model.
     */
    @Transactional
    public void deleteModel(String tenantId, UUID modelId) {
        log.info("Deleting model: {} for tenant: {}", modelId, tenantId);
        if (!modelRepository.exists(tenantId, modelId)) {
            throw new IllegalArgumentException("Model not found: " + modelId);
        }
        modelRepository.delete(tenantId, modelId);
    }

    // ==================== Time Series Data Operations ====================

    /**
     * Submit time series data.
     */
    @Transactional
    public TimeSeriesDataDto submitTimeSeriesData(UpdateTimeSeriesDataCommand command) {
        log.info("Submitting time series data: {} for tenant: {}", command.getDataSourceName(), command.getTenantId());

        TimeSeriesData timeSeriesData = TimeSeriesData.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .dataSourceName(command.getDataSourceName())
                .description(command.getDescription())
                .granularity(command.getGranularity())
                .dataPoints(command.getDataPoints())
                .frequencyType(command.getFrequencyType())
                .hasSeasonality(command.getHasSeasonality())
                .hasTrend(command.getHasTrend())
                .seasonalityPeriod(command.getSeasonalityPeriod())
                .metadata(command.getMetadata())
                .totalDataPoints(command.getDataPoints() != null ? command.getDataPoints().size() : 0)
                .createdAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .build();

        // Set date range from data points
        if (command.getDataPoints() != null && !command.getDataPoints().isEmpty()) {
            timeSeriesData.setStartDate(command.getDataPoints().get(0).getTimestamp());
            timeSeriesData.setEndDate(command.getDataPoints().get(command.getDataPoints().size() - 1).getTimestamp());
        }

        TimeSeriesData savedData = timeSeriesDataRepository.save(command.getTenantId(), timeSeriesData);

        log.info("Time series data submitted successfully: {}", savedData.getId());
        return timeSeriesDataMapper.toDto(savedData);
    }

    /**
     * Get time series data by ID.
     */
    @Transactional(readOnly = true)
    public TimeSeriesDataDto getTimeSeriesData(String tenantId, UUID dataId) {
        log.info("Getting time series data: {} for tenant: {}", dataId, tenantId);
        TimeSeriesData data = timeSeriesDataRepository.findById(tenantId, dataId)
                .orElseThrow(() -> new IllegalArgumentException("Time series data not found: " + dataId));
        return timeSeriesDataMapper.toDto(data);
    }

    /**
     * List all time series data.
     */
    @Transactional(readOnly = true)
    public List<TimeSeriesDataDto> listTimeSeriesData(String tenantId) {
        log.info("Listing time series data for tenant: {}", tenantId);
        return timeSeriesDataRepository.findByTenantId(tenantId).stream()
                .map(timeSeriesDataMapper::toDto)
                .collect(Collectors.toList());
    }

    // ==================== Configuration Operations ====================

    /**
     * Create a forecast configuration.
     */
    @Transactional
    public ForecastConfigurationDto createConfiguration(CreateForecastConfigurationCommand command) {
        log.info("Creating configuration: {} for tenant: {}", command.getConfigName(), command.getTenantId());

        ForecastConfiguration configuration = ForecastConfiguration.builder()
                .id(UUID.randomUUID())
                .tenantId(command.getTenantId())
                .configName(command.getConfigName())
                .description(command.getDescription())
                .modelType(command.getModelType())
                .forecastHorizon(command.getForecastHorizon())
                .granularity(command.getGranularity())
                .confidenceLevel(command.getConfidenceLevel())
                .enableSeasonality(command.getEnableSeasonality())
                .seasonalityPeriod(command.getSeasonalityPeriod())
                .enableTrend(command.getEnableTrend())
                .modelParameters(command.getModelParameters())
                .preprocessingConfig(command.getPreprocessingConfig())
                .postprocessingConfig(command.getPostprocessingConfig())
                .maxHistoryDataPoints(command.getMaxHistoryDataPoints())
                .minHistoryDataPoints(command.getMinHistoryDataPoints())
                .enableOutlierDetection(command.getEnableOutlierDetection())
                .outlierThreshold(command.getOutlierThreshold())
                .enableAnomalyDetection(command.getEnableAnomalyDetection())
                .metadata(command.getMetadata())
                .isActive(command.getIsActive() != null ? command.getIsActive() : true)
                .createdAt(LocalDateTime.now())
                .createdBy(command.getCreatedBy())
                .build();

        // Note: Configuration would need its own repository, for now returning DTO directly
        log.info("Configuration created successfully: {}", configuration.getId());
        return configurationMapper.toDto(configuration);
    }

    /**
     * Get forecast accuracy metrics.
     */
    @Transactional(readOnly = true)
    public ForecastAccuracyDto getForecastAccuracy(GetForecastAccuracyQuery query) {
        log.info("Getting forecast accuracy for: {} tenant: {}", query.getForecastId(), query.getTenantId());

        Forecast forecast = forecastRepository.findById(query.getTenantId(), query.getForecastId())
                .orElseThrow(() -> new IllegalArgumentException("Forecast not found: " + query.getForecastId()));

        // Simulate accuracy calculation
        ForecastAccuracy accuracy = ForecastAccuracy.builder()
                .id(UUID.randomUUID())
                .tenantId(query.getTenantId())
                .forecastId(query.getForecastId())
                .modelId(forecast.getModelId())
                .meanAbsoluteError(new BigDecimal("10.5"))
                .meanSquaredError(new BigDecimal("110.25"))
                .rootMeanSquaredError(new BigDecimal("10.5"))
                .meanAbsolutePercentageError(new BigDecimal("0.08"))
                .symmetricMeanAbsolutePercentageError(new BigDecimal("0.075"))
                .rSquared(new BigDecimal("0.92"))
                .correctDirectionPredictions(85)
                .totalPredictions(100)
                .directionAccuracy(new BigDecimal("0.85"))
                .calculatedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .createdBy("system")
                .build();

        return ForecastAccuracyDto.builder()
                .id(accuracy.getId())
                .tenantId(accuracy.getTenantId())
                .forecastId(accuracy.getForecastId())
                .modelId(accuracy.getModelId())
                .meanAbsoluteError(accuracy.getMeanAbsoluteError())
                .meanSquaredError(accuracy.getMeanSquaredError())
                .rootMeanSquaredError(accuracy.getRootMeanSquaredError())
                .meanAbsolutePercentageError(accuracy.getMeanAbsolutePercentageError())
                .symmetricMeanAbsolutePercentageError(accuracy.getSymmetricMeanAbsolutePercentageError())
                .rSquared(accuracy.getRSquared())
                .correctDirectionPredictions(accuracy.getCorrectDirectionPredictions())
                .totalPredictions(accuracy.getTotalPredictions())
                .directionAccuracy(accuracy.getDirectionAccuracy())
                .calculatedAt(accuracy.getCalculatedAt())
                .createdAt(accuracy.getCreatedAt())
                .createdBy(accuracy.getCreatedBy())
                .build();
    }
}
