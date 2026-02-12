package com.gogidix.rapidassist.ai.predictive.analytics.interfaces.rest;

import com.gogidix.rapidassist.ai.predictive.analytics.application.dto.ForecastDto;
import com.gogidix.rapidassist.ai.predictive.analytics.application.service.ForecastService;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Forecast;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Forecast management
 */
@RestController
@RequestMapping("/api/v1/forecasts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Forecasts", description = "APIs for managing forecasts")
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping
    @Operation(summary = "Create a new forecast")
    public ResponseEntity<ForecastDto> createForecast(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateForecastRequest request) {

        Forecast forecast = forecastService.createForecast(
                tenantId,
                request.getModelId(),
                request.getForecastName(),
                request.getStartDate(),
                request.getEndDate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(forecast));
    }

    @PostMapping("/{forecastId}/generate")
    @Operation(summary = "Generate forecast")
    public ResponseEntity<ForecastDto> generateForecast(
            @Parameter(description = "Forecast ID", required = true) @PathVariable UUID forecastId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        Forecast forecast = forecastService.generateForecast(forecastId, tenantId);
        return ResponseEntity.ok(toDto(forecast));
    }

    @GetMapping("/{forecastId}")
    @Operation(summary = "Get a forecast by ID")
    public ResponseEntity<ForecastDto> getForecast(
            @Parameter(description = "Forecast ID", required = true) @PathVariable UUID forecastId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        Forecast forecast = forecastService.getForecastById(forecastId, tenantId);
        return ResponseEntity.ok(toDto(forecast));
    }

    @GetMapping
    @Operation(summary = "Get forecasts with filters")
    public ResponseEntity<List<ForecastDto>> getForecasts(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by model ID") @RequestParam(required = false) UUID modelId,
            @Parameter(description = "Filter by generation start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by generation end date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Limit results") @RequestParam(required = false, defaultValue = "50") int limit) {

        List<Forecast> forecasts;

        if (modelId != null) {
            forecasts = forecastService.getForecastsByModel(modelId, tenantId);
        } else if (startDate != null && endDate != null) {
            forecasts = forecastService.getForecastsByDateRange(startDate, endDate, tenantId);
        } else {
            forecasts = forecastService.getRecentForecasts(tenantId, limit);
        }

        return ResponseEntity.ok(forecasts.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{forecastId}")
    @Operation(summary = "Delete a forecast")
    public ResponseEntity<Void> deleteForecast(
            @Parameter(description = "Forecast ID", required = true) @PathVariable UUID forecastId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        forecastService.deleteForecast(forecastId, tenantId);
        return ResponseEntity.noContent().build();
    }

    private ForecastDto toDto(Forecast forecast) {
        List<ForecastDto.ForecastDataPointDto> dataPointDtos = null;
        if (forecast.getForecastData() != null) {
            dataPointDtos = forecast.getForecastData().stream()
                    .map(point -> ForecastDto.ForecastDataPointDto.builder()
                            .timestamp(point.getTimestamp())
                            .value(point.getValue())
                            .confidence(point.getConfidence())
                            .build())
                    .collect(Collectors.toList());
        }

        return ForecastDto.builder()
                .id(forecast.getId())
                .tenantId(forecast.getTenantId())
                .modelId(forecast.getModelId())
                .forecastName(forecast.getForecastName())
                .forecastStartDate(forecast.getForecastStartDate())
                .forecastEndDate(forecast.getForecastEndDate())
                .forecastHorizon(forecast.getForecastHorizon())
                .forecastData(dataPointDtos)
                .meanAbsoluteError(forecast.getMeanAbsoluteError())
                .meanAbsolutePercentageError(forecast.getMeanAbsolutePercentageError())
                .status(forecast.getStatus() != null ? forecast.getStatus().name() : null)
                .frequency(forecast.getFrequency())
                .createdAt(forecast.getCreatedAt())
                .generatedAt(forecast.getGeneratedAt())
                .build();
    }

    // Request DTOs
    public static class CreateForecastRequest {
        private UUID modelId;
        private String forecastName;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startDate;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endDate;

        public UUID getModelId() { return modelId; }
        public void setModelId(UUID modelId) { this.modelId = modelId; }
        public String getForecastName() { return forecastName; }
        public void setForecastName(String forecastName) { this.forecastName = forecastName; }
        public LocalDateTime getStartDate() { return startDate; }
        public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }
        public LocalDateTime getEndDate() { return endDate; }
        public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }
    }
}
