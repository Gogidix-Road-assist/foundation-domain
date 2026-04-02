package com.gogidix.rapidassist.ai.inference.interfaces.rest;

import com.gogidix.rapidassist.ai.inference.application.command.CreateModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.command.DeployModelVersionCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.ModelVersionDto;
import com.gogidix.rapidassist.ai.inference.application.port.in.ModelVersionUseCase;
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
 * REST controller for Model Version operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/model-versions")
@RequiredArgsConstructor
@Tag(name = "Model Versions", description = "Model Version Management API")
public class ModelVersionController {

    private final ModelVersionUseCase modelVersionUseCase;

    @PostMapping
    @Operation(summary = "Create a new model version")
    public ResponseEntity<ModelVersionDto> createModelVersion(
            @Valid @RequestBody CreateModelVersionCommand command) {
        log.info("POST /api/v1/model-versions - Creating model version");
        ModelVersionDto result = modelVersionUseCase.createModelVersion(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get model version by ID")
    public ResponseEntity<ModelVersionDto> getModelVersionById(
            @Parameter(description = "Model version ID") @PathVariable UUID id) {
        log.info("GET /api/v1/model-versions/{}", id);
        ModelVersionDto result = modelVersionUseCase.getModelVersionById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/model/{modelId}/version/{version}")
    @Operation(summary = "Get model version by model ID and version")
    public ResponseEntity<ModelVersionDto> getModelVersion(
            @Parameter(description = "Model ID") @PathVariable String modelId,
            @Parameter(description = "Version") @PathVariable String version) {
        log.info("GET /api/v1/model-versions/model/{}/version/{}", modelId, version);
        ModelVersionDto result = modelVersionUseCase.getModelVersion(modelId, version);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/model/{modelId}")
    @Operation(summary = "Get all versions for a model")
    public ResponseEntity<List<ModelVersionDto>> getModelVersions(
            @Parameter(description = "Model ID") @PathVariable String modelId) {
        log.info("GET /api/v1/model-versions/model/{}", modelId);
        List<ModelVersionDto> results = modelVersionUseCase.getModelVersions(modelId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get model versions by tenant")
    public ResponseEntity<List<ModelVersionDto>> getModelVersionsByTenant(
            @Parameter(description = "Tenant ID") @PathVariable String tenantId) {
        log.info("GET /api/v1/model-versions/tenant/{}", tenantId);
        List<ModelVersionDto> results = modelVersionUseCase.getModelVersionsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/model/{modelId}/default")
    @Operation(summary = "Get default model version")
    public ResponseEntity<ModelVersionDto> getDefaultModelVersion(
            @Parameter(description = "Model ID") @PathVariable String modelId) {
        log.info("GET /api/v1/model-versions/model/{}/default", modelId);
        ModelVersionDto result = modelVersionUseCase.getDefaultModelVersion(modelId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/deploy")
    @Operation(summary = "Deploy a model version")
    public ResponseEntity<ModelVersionDto> deployModelVersion(
            @Valid @RequestBody DeployModelVersionCommand command) {
        log.info("POST /api/v1/model-versions/deploy");
        ModelVersionDto result = modelVersionUseCase.deployModelVersion(command);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/retire")
    @Operation(summary = "Retire a model version")
    public ResponseEntity<Void> retireModelVersion(
            @Parameter(description = "Model version ID") @PathVariable UUID id) {
        log.info("POST /api/v1/model-versions/{}/retire", id);
        modelVersionUseCase.retireModelVersion(id);
        return ResponseEntity.ok().build();
    }
}
