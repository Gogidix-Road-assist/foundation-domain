package com.gogidix.rapidassist.ai.forecasting.interfaces.rest.controller;

import com.gogidix.rapidassist.ai.forecasting.application.command.*;
import com.gogidix.rapidassist.ai.forecasting.application.dto.*;
import com.gogidix.rapidassist.ai.forecasting.application.query.*;
import com.gogidix.rapidassist.ai.forecasting.application.service.ForecastingApplicationService;
import com.gogidix.rapidassist.ai.forecasting.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.forecasting.interfaces.rest.request.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for AI Forecasting Service operations.
 * API endpoint: /api/v1/forecasting
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/forecasting")
@RequiredArgsConstructor
@Tag(name = "Forecasting", description = "AI Forecasting APIs")
public class ForecastingController {

    private final ForecastingApplicationService applicationService;

    // ==================== Forecast Operations ====================

    /**
     * Generate a forecast.
     * POST /api/v1/forecasting/generate
     */
    @PostMapping("/generate")
    @Operation(summary = "Generate forecast", description = "Generates a new AI-powered forecast")
    public ResponseEntity<ForecastDto> generateForecast(
            @Valid @RequestBody GenerateForecastRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var command = GenerateForecastCommand.builder()
                .tenantId(tenantId)
                .forecastName(request.getForecastName())
                .description(request.getDescription())
                .modelId(request.getModelId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .forecastHorizon(request.getForecastHorizon())
                .granularity(request.getGranularity())
                .parameters(request.getParameters())
                .metadata(request.getMetadata())
                .createdBy("api-user")
                .build();

        ForecastDto forecastDto = applicationService.generateForecast(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(forecastDto);
    }

    /**
     * Get a forecast by ID.
     * GET /api/v1/forecasting/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get forecast", description = "Retrieves a forecast by ID")
    public ResponseEntity<ForecastDto> getForecast(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Include data points") @RequestParam(defaultValue = "false") Boolean includeDataPoints) {

        // Tenant context is already set by TenantInterceptor
        var query = GetForecastQuery.builder()
                .tenantId(tenantId)
                .forecastId(id)
                .includeDataPoints(includeDataPoints)
                .build();

        ForecastDto forecastDto = applicationService.getForecast(query);

        return ResponseEntity.ok(forecastDto);
    }

    /**
     * List all forecasts.
     * GET /api/v1/forecasting
     */
    @GetMapping
    @Operation(summary = "List forecasts", description = "Lists all forecasts with pagination")
    public ResponseEntity<List<ForecastDto>> listForecasts(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") Integer size,
            @Parameter(description = "Sort by") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {

        // Tenant context is already set by TenantInterceptor
        var query = ListForecastsQuery.builder()
                .tenantId(tenantId)
                .status(status)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        List<ForecastDto> forecasts = applicationService.listForecasts(query);

        return ResponseEntity.ok(forecasts);
    }

    /**
     * Delete a forecast.
     * DELETE /api/v1/forecasting/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete forecast", description = "Deletes a forecast by ID")
    public ResponseEntity<Void> deleteForecast(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        applicationService.deleteForecast(tenantId, id);

        return ResponseEntity.noContent().build();
    }

    // ==================== Model Operations ====================

    /**
     * Create a forecast model.
     * POST /api/v1/forecasting/models
     */
    @PostMapping("/models")
    @Operation(summary = "Create forecast model", description = "Creates a new forecasting model")
    public ResponseEntity<ForecastModelDto> createModel(
            @Valid @RequestBody CreateForecastModelRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var command = CreateForecastModelCommand.builder()
                .tenantId(tenantId)
                .modelName(request.getModelName())
                .description(request.getDescription())
                .modelType(request.getModelType())
                .version(request.getVersion())
                .hyperparameters(request.getHyperparameters())
                .trainingParameters(request.getTrainingParameters())
                .metadata(request.getMetadata())
                .createdBy("api-user")
                .build();

        ForecastModelDto modelDto = applicationService.createModel(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(modelDto);
    }

    /**
     * Update a forecast model.
     * PUT /api/v1/forecasting/models/{id}
     */
    @PutMapping("/models/{id}")
    @Operation(summary = "Update forecast model", description = "Updates a forecasting model")
    public ResponseEntity<ForecastModelDto> updateModel(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateForecastModelRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var command = UpdateForecastModelCommand.builder()
                .tenantId(tenantId)
                .modelId(id)
                .modelName(request.getModelName())
                .description(request.getDescription())
                .status(request.getStatus())
                .version(request.getVersion())
                .hyperparameters(request.getHyperparameters())
                .trainingParameters(request.getTrainingParameters())
                .metadata(request.getMetadata())
                .updatedBy("api-user")
                .build();

        ForecastModelDto modelDto = applicationService.updateModel(command);

        return ResponseEntity.ok(modelDto);
    }

    /**
     * List all models.
     * GET /api/v1/forecasting/models
     */
    @GetMapping("/models")
    @Operation(summary = "List models", description = "Lists all forecasting models")
    public ResponseEntity<List<ForecastModelDto>> listModels(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        List<ForecastModelDto> models = applicationService.listModels(tenantId);

        return ResponseEntity.ok(models);
    }

    /**
     * Get a model by ID.
     * GET /api/v1/forecasting/models/{id}
     */
    @GetMapping("/models/{id}")
    @Operation(summary = "Get model", description = "Retrieves a forecasting model by ID")
    public ResponseEntity<ForecastModelDto> getModel(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        ForecastModelDto modelDto = applicationService.getModel(tenantId, id);

        return ResponseEntity.ok(modelDto);
    }

    /**
     * Delete a model.
     * DELETE /api/v1/forecasting/models/{id}
     */
    @DeleteMapping("/models/{id}")
    @Operation(summary = "Delete model", description = "Deletes a forecasting model")
    public ResponseEntity<Void> deleteModel(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        applicationService.deleteModel(tenantId, id);

        return ResponseEntity.noContent().build();
    }

    // ==================== Time Series Data Operations ====================

    /**
     * Submit time series data.
     * POST /api/v1/forecasting/data/timeseries
     */
    @PostMapping("/data/timeseries")
    @Operation(summary = "Submit time series data", description = "Submits time series data for forecasting")
    public ResponseEntity<TimeSeriesDataDto> submitTimeSeriesData(
            @Valid @RequestBody SubmitTimeSeriesDataRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var command = UpdateTimeSeriesDataCommand.builder()
                .tenantId(tenantId)
                .dataSourceName(request.getDataSourceName())
                .description(request.getDescription())
                .granularity(request.getGranularity())
                .dataPoints(request.getDataPoints())
                .frequencyType(request.getFrequencyType())
                .hasSeasonality(request.getHasSeasonality())
                .hasTrend(request.getHasTrend())
                .seasonalityPeriod(request.getSeasonalityPeriod())
                .metadata(request.getMetadata())
                .createdBy("api-user")
                .build();

        TimeSeriesDataDto dataDto = applicationService.submitTimeSeriesData(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(dataDto);
    }

    /**
     * List time series data.
     * GET /api/v1/forecasting/data/timeseries
     */
    @GetMapping("/data/timeseries")
    @Operation(summary = "List time series data", description = "Lists all time series data")
    public ResponseEntity<List<TimeSeriesDataDto>> listTimeSeriesData(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        List<TimeSeriesDataDto> dataList = applicationService.listTimeSeriesData(tenantId);

        return ResponseEntity.ok(dataList);
    }

    /**
     * Get time series data by ID.
     * GET /api/v1/forecasting/data/timeseries/{id}
     */
    @GetMapping("/data/timeseries/{id}")
    @Operation(summary = "Get time series data", description = "Retrieves time series data by ID")
    public ResponseEntity<TimeSeriesDataDto> getTimeSeriesData(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        TimeSeriesDataDto dataDto = applicationService.getTimeSeriesData(tenantId, id);

        return ResponseEntity.ok(dataDto);
    }

    // ==================== Accuracy Operations ====================

    /**
     * Get forecast accuracy metrics.
     * GET /api/v1/forecasting/accuracy/{id}
     */
    @GetMapping("/accuracy/{id}")
    @Operation(summary = "Get forecast accuracy", description = "Retrieves accuracy metrics for a forecast")
    public ResponseEntity<ForecastAccuracyDto> getForecastAccuracy(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var query = GetForecastAccuracyQuery.builder()
                .tenantId(tenantId)
                .forecastId(id)
                .build();

        ForecastAccuracyDto accuracyDto = applicationService.getForecastAccuracy(query);

        return ResponseEntity.ok(accuracyDto);
    }

    // ==================== Configuration Operations ====================

    /**
     * Create a forecast configuration.
     * POST /api/v1/forecasting/configurations
     */
    @PostMapping("/configurations")
    @Operation(summary = "Create configuration", description = "Creates a forecast configuration")
    public ResponseEntity<ForecastConfigurationDto> createConfiguration(
            @Valid @RequestBody CreateForecastConfigurationRequest request,
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        var command = CreateForecastConfigurationCommand.builder()
                .tenantId(tenantId)
                .configName(request.getConfigName())
                .description(request.getDescription())
                .modelType(request.getModelType())
                .forecastHorizon(request.getForecastHorizon())
                .granularity(request.getGranularity())
                .confidenceLevel(request.getConfidenceLevel())
                .enableSeasonality(request.getEnableSeasonality())
                .seasonalityPeriod(request.getSeasonalityPeriod())
                .enableTrend(request.getEnableTrend())
                .modelParameters(request.getModelParameters())
                .preprocessingConfig(request.getPreprocessingConfig())
                .postprocessingConfig(request.getPostprocessingConfig())
                .maxHistoryDataPoints(request.getMaxHistoryDataPoints())
                .minHistoryDataPoints(request.getMinHistoryDataPoints())
                .enableOutlierDetection(request.getEnableOutlierDetection())
                .outlierThreshold(request.getOutlierThreshold())
                .enableAnomalyDetection(request.getEnableAnomalyDetection())
                .metadata(request.getMetadata())
                .isActive(request.getIsActive())
                .createdBy("api-user")
                .build();

        ForecastConfigurationDto configDto = applicationService.createConfiguration(command);

        return ResponseEntity.status(HttpStatus.CREATED).body(configDto);
    }

    /**
     * List all configurations.
     * GET /api/v1/forecasting/configurations
     */
    @GetMapping("/configurations")
    @Operation(summary = "List configurations", description = "Lists all forecast configurations")
    public ResponseEntity<List<ForecastConfigurationDto>> listConfigurations(
            @RequestHeader("X-Tenant-ID") String tenantId) {

        // Tenant context is already set by TenantInterceptor
        // Note: This would need a listConfigurations method in service
        // For now returning empty list as placeholder
        return ResponseEntity.ok(List.of());
    }
}
