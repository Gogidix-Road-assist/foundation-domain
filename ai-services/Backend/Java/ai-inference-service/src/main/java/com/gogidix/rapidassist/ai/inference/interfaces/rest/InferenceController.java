package com.gogidix.rapidassist.ai.inference.interfaces.rest;

import com.gogidix.rapidassist.ai.inference.application.command.CreateInferenceRequestCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.InferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.application.port.in.InferenceUseCase;
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
 * REST controller for Inference operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/inference")
@RequiredArgsConstructor
@Tag(name = "Inference", description = "AI Inference API")
public class InferenceController {

    private final InferenceUseCase inferenceUseCase;

    @PostMapping
    @Operation(summary = "Create a new inference request")
    public ResponseEntity<InferenceRequestDto> createInferenceRequest(
            @Valid @RequestBody CreateInferenceRequestCommand command) {
        log.info("POST /api/v1/inference - Creating inference request");
        InferenceRequestDto result = inferenceUseCase.createInferenceRequest(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inference request by ID")
    public ResponseEntity<InferenceRequestDto> getInferenceById(
            @Parameter(description = "Inference request ID") @PathVariable UUID id) {
        log.info("GET /api/v1/inference/{}", id);
        InferenceRequestDto result = inferenceUseCase.getInferenceRequestById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "Get inference request by request ID")
    public ResponseEntity<InferenceRequestDto> getInferenceByRequestId(
            @Parameter(description = "Request ID") @PathVariable String requestId) {
        log.info("GET /api/v1/inference/request/{}", requestId);
        InferenceRequestDto result = inferenceUseCase.getInferenceRequestByRequestId(requestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get inference requests by tenant")
    public ResponseEntity<List<InferenceRequestDto>> getInferencesByTenant(
            @Parameter(description = "Tenant ID") @PathVariable String tenantId) {
        log.info("GET /api/v1/inference/tenant/{}", tenantId);
        List<InferenceRequestDto> results = inferenceUseCase.getInferenceRequestsByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/model/{modelId}")
    @Operation(summary = "Get inference requests by model")
    public ResponseEntity<List<InferenceRequestDto>> getInferencesByModel(
            @Parameter(description = "Model ID") @PathVariable String modelId) {
        log.info("GET /api/v1/inference/model/{}", modelId);
        List<InferenceRequestDto> results = inferenceUseCase.getInferenceRequestsByModel(modelId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process an inference request")
    public ResponseEntity<Void> processInference(
            @Parameter(description = "Inference request ID") @PathVariable UUID id) {
        log.info("POST /api/v1/inference/{}/process", id);
        inferenceUseCase.processInference(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete an inference request")
    public ResponseEntity<Void> completeInference(
            @Parameter(description = "Inference request ID") @PathVariable UUID id,
            @RequestBody String outputData) {
        log.info("POST /api/v1/inference/{}/complete", id);
        inferenceUseCase.completeInference(id, outputData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Fail an inference request")
    public ResponseEntity<Void> failInference(
            @Parameter(description = "Inference request ID") @PathVariable UUID id,
            @RequestBody String errorMessage) {
        log.info("POST /api/v1/inference/{}/fail", id);
        inferenceUseCase.failInference(id, errorMessage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending inference requests")
    public ResponseEntity<List<InferenceRequestDto>> getPendingInferences() {
        log.info("GET /api/v1/inference/pending");
        List<InferenceRequestDto> results = inferenceUseCase.getPendingInferences();
        return ResponseEntity.ok(results);
    }
}
