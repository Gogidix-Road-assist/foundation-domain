package com.gogidix.rapidassist.ai.predictive.analytics.interfaces.rest;

import com.gogidix.rapidassist.ai.predictive.analytics.application.dto.PredictionDto;
import com.gogidix.rapidassist.ai.predictive.analytics.application.service.PredictionService;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.Prediction;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictionStatus;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Prediction management
 */
@RestController
@RequestMapping("/api/v1/predictions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Predictions", description = "APIs for managing predictions")
public class PredictionController {

    private final PredictionService predictionService;

    @PostMapping
    @Operation(summary = "Create a new prediction request")
    public ResponseEntity<PredictionDto> createPrediction(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreatePredictionRequest request) {

        Prediction prediction = predictionService.createPrediction(
                tenantId,
                request.getModelId(),
                request.getInputData()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(prediction));
    }

    @PostMapping("/{predictionId}/process")
    @Operation(summary = "Process a prediction request")
    public ResponseEntity<PredictionDto> processPrediction(
            @Parameter(description = "Prediction ID", required = true) @PathVariable UUID predictionId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        Prediction prediction = predictionService.processPrediction(predictionId, tenantId);
        return ResponseEntity.ok(toDto(prediction));
    }

    @GetMapping("/{predictionId}")
    @Operation(summary = "Get a prediction by ID")
    public ResponseEntity<PredictionDto> getPrediction(
            @Parameter(description = "Prediction ID", required = true) @PathVariable UUID predictionId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        Prediction prediction = predictionService.getPredictionById(predictionId, tenantId);
        return ResponseEntity.ok(toDto(prediction));
    }

    @GetMapping
    @Operation(summary = "Get predictions with filters")
    public ResponseEntity<List<PredictionDto>> getPredictions(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by model ID") @RequestParam(required = false) UUID modelId,
            @Parameter(description = "Filter by status") @RequestParam(required = false) PredictionStatus status,
            @Parameter(description = "Filter by start date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filter by end date") @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @Parameter(description = "Limit results") @RequestParam(required = false, defaultValue = "50") int limit) {

        List<Prediction> predictions;

        if (modelId != null) {
            predictions = predictionService.getPredictionsByModel(modelId, tenantId);
        } else if (status != null) {
            predictions = predictionService.getPredictionsByStatus(status, tenantId);
        } else if (startDate != null && endDate != null) {
            predictions = predictionService.getPredictionsByDateRange(startDate, endDate, tenantId);
        } else {
            predictions = predictionService.getRecentPredictions(tenantId, limit);
        }

        return ResponseEntity.ok(predictions.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    private PredictionDto toDto(Prediction prediction) {
        return PredictionDto.builder()
                .id(prediction.getId())
                .tenantId(prediction.getTenantId())
                .modelId(prediction.getModelId())
                .modelName(prediction.getModelName())
                .inputData(prediction.getInputData())
                .predictionResult(prediction.getPredictionResult())
                .confidenceScore(prediction.getConfidenceScore())
                .status(prediction.getStatus())
                .errorMessage(prediction.getErrorMessage())
                .createdAt(prediction.getCreatedAt())
                .completedAt(prediction.getCompletedAt())
                .processingTimeMs(prediction.getProcessingTimeMs())
                .build();
    }

    // Request DTOs
    public static class CreatePredictionRequest {
        private UUID modelId;
        private Map<String, Object> inputData;

        public UUID getModelId() { return modelId; }
        public void setModelId(UUID modelId) { this.modelId = modelId; }
        public Map<String, Object> getInputData() { return inputData; }
        public void setInputData(Map<String, Object> inputData) { this.inputData = inputData; }
    }
}
