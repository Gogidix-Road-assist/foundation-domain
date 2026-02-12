package com.gogidix.rapidassist.ai.predictive.analytics.interfaces.rest;

import com.gogidix.rapidassist.ai.predictive.analytics.application.dto.PredictiveModelDto;
import com.gogidix.rapidassist.ai.predictive.analytics.application.service.PredictiveModelService;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelStatus;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.ModelType;
import com.gogidix.rapidassist.ai.predictive.analytics.domain.model.PredictiveModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Controller for Predictive Model management
 */
@RestController
@RequestMapping("/api/v1/predictive-models")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Predictive Models", description = "APIs for managing predictive models")
public class PredictiveModelController {

    private final PredictiveModelService modelService;

    @PostMapping
    @Operation(summary = "Create a new predictive model")
    public ResponseEntity<PredictiveModelDto> createModel(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody CreateModelRequest request) {

        PredictiveModel model = modelService.createModel(
                tenantId,
                request.getName(),
                request.getDescription(),
                request.getModelType(),
                request.getAlgorithm()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(model));
    }

    @GetMapping("/{modelId}")
    @Operation(summary = "Get a predictive model by ID")
    public ResponseEntity<PredictiveModelDto> getModel(
            @Parameter(description = "Model ID", required = true) @PathVariable UUID modelId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        PredictiveModel model = modelService.getModelById(modelId, tenantId);
        return ResponseEntity.ok(toDto(model));
    }

    @GetMapping
    @Operation(summary = "Get all predictive models for a tenant")
    public ResponseEntity<List<PredictiveModelDto>> getAllModels(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Filter by model type") @RequestParam(required = false) ModelType modelType) {

        List<PredictiveModel> models;
        if (modelType != null) {
            models = modelService.getModelsByType(modelType, tenantId);
        } else {
            models = modelService.getAllModels(tenantId);
        }

        return ResponseEntity.ok(models.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get all active predictive models")
    public ResponseEntity<List<PredictiveModelDto>> getActiveModels(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        List<PredictiveModel> models = modelService.getActiveModels(tenantId);
        return ResponseEntity.ok(models.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{modelId}/status")
    @Operation(summary = "Update model status")
    public ResponseEntity<PredictiveModelDto> updateModelStatus(
            @Parameter(description = "Model ID", required = true) @PathVariable UUID modelId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Valid @RequestBody UpdateStatusRequest request) {

        PredictiveModel model = modelService.updateModelStatus(modelId, tenantId, request.getStatus());
        return ResponseEntity.ok(toDto(model));
    }

    @PutMapping("/{modelId}/metrics")
    @Operation(summary = "Update model performance metrics")
    public ResponseEntity<PredictiveModelDto> updateModelMetrics(
            @Parameter(description = "Model ID", required = true) @PathVariable UUID modelId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestBody Map<String, Double> metrics) {

        PredictiveModel model = modelService.updateModelMetrics(modelId, tenantId, metrics);
        return ResponseEntity.ok(toDto(model));
    }

    @DeleteMapping("/{modelId}")
    @Operation(summary = "Delete a predictive model")
    public ResponseEntity<Void> deleteModel(
            @Parameter(description = "Model ID", required = true) @PathVariable UUID modelId,
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId) {

        modelService.deleteModel(modelId, tenantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/retraining-needed")
    @Operation(summary = "Get models needing retraining")
    public ResponseEntity<List<PredictiveModelDto>> getModelsNeedingRetraining(
            @Parameter(description = "Tenant ID", required = true) @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Threshold in days", required = true) @RequestParam(defaultValue = "30") int thresholdDays) {

        List<PredictiveModel> models = modelService.getModelsNeedingRetraining(tenantId, thresholdDays);
        return ResponseEntity.ok(models.stream()
                .map(this::toDto)
                .collect(Collectors.toList()));
    }

    private PredictiveModelDto toDto(PredictiveModel model) {
        return PredictiveModelDto.builder()
                .id(model.getId())
                .tenantId(model.getTenantId())
                .name(model.getName())
                .description(model.getDescription())
                .modelType(model.getModelType())
                .status(model.getStatus())
                .algorithm(model.getAlgorithm())
                .performanceMetrics(model.getPerformanceMetrics())
                .modelVersion(model.getModelVersion())
                .features(model.getFeatures())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .lastTrainedAt(model.getLastTrainedAt())
                .deployedAt(model.getDeployedAt())
                .build();
    }

    // Request DTOs
    public static class CreateModelRequest {
        private String name;
        private String description;
        private ModelType modelType;
        private String algorithm;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public ModelType getModelType() { return modelType; }
        public void setModelType(ModelType modelType) { this.modelType = modelType; }
        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    }

    public static class UpdateStatusRequest {
        private ModelStatus status;

        public ModelStatus getStatus() { return status; }
        public void setStatus(ModelStatus status) { this.status = status; }
    }
}
