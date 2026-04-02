package com.gogidix.rapidassist.ai.inference.interfaces.rest;

import com.gogidix.rapidassist.ai.inference.application.command.CreateBatchInferenceCommand;
import com.gogidix.rapidassist.ai.inference.application.dto.BatchInferenceRequestDto;
import com.gogidix.rapidassist.ai.inference.application.port.in.BatchInferenceUseCase;
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
 * REST controller for Batch Inference operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/batch-inference")
@RequiredArgsConstructor
@Tag(name = "Batch Inference", description = "Batch Inference API")
public class BatchInferenceController {

    private final BatchInferenceUseCase batchInferenceUseCase;

    @PostMapping
    @Operation(summary = "Create a new batch inference request")
    public ResponseEntity<BatchInferenceRequestDto> createBatchInference(
            @Valid @RequestBody CreateBatchInferenceCommand command) {
        log.info("POST /api/v1/batch-inference - Creating batch inference request");
        BatchInferenceRequestDto result = batchInferenceUseCase.createBatchInference(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get batch inference request by ID")
    public ResponseEntity<BatchInferenceRequestDto> getBatchInferenceById(
            @Parameter(description = "Batch request ID") @PathVariable UUID id) {
        log.info("GET /api/v1/batch-inference/{}", id);
        BatchInferenceRequestDto result = batchInferenceUseCase.getBatchInferenceById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/batch/{batchId}")
    @Operation(summary = "Get batch inference request by batch ID")
    public ResponseEntity<BatchInferenceRequestDto> getBatchInferenceByBatchId(
            @Parameter(description = "Batch ID") @PathVariable String batchId) {
        log.info("GET /api/v1/batch-inference/batch/{}", batchId);
        BatchInferenceRequestDto result = batchInferenceUseCase.getBatchInferenceByBatchId(batchId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/tenant/{tenantId}")
    @Operation(summary = "Get batch inference requests by tenant")
    public ResponseEntity<List<BatchInferenceRequestDto>> getBatchInferencesByTenant(
            @Parameter(description = "Tenant ID") @PathVariable String tenantId) {
        log.info("GET /api/v1/batch-inference/tenant/{}", tenantId);
        List<BatchInferenceRequestDto> results = batchInferenceUseCase.getBatchInferencesByTenant(tenantId);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process a batch inference request")
    public ResponseEntity<Void> processBatchInference(
            @Parameter(description = "Batch request ID") @PathVariable UUID id) {
        log.info("POST /api/v1/batch-inference/{}/process", id);
        batchInferenceUseCase.processBatchInference(id);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete a batch inference request")
    public ResponseEntity<Void> completeBatchInference(
            @Parameter(description = "Batch request ID") @PathVariable UUID id) {
        log.info("POST /api/v1/batch-inference/{}/complete", id);
        batchInferenceUseCase.completeBatchInference(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/fail")
    @Operation(summary = "Fail a batch inference request")
    public ResponseEntity<Void> failBatchInference(
            @Parameter(description = "Batch request ID") @PathVariable UUID id,
            @RequestBody String errorMessage) {
        log.info("POST /api/v1/batch-inference/{}/fail", id);
        batchInferenceUseCase.failBatchInference(id, errorMessage);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    @Operation(summary = "Get all pending batch inference requests")
    public ResponseEntity<List<BatchInferenceRequestDto>> getPendingBatches() {
        log.info("GET /api/v1/batch-inference/pending");
        List<BatchInferenceRequestDto> results = batchInferenceUseCase.getPendingBatches();
        return ResponseEntity.ok(results);
    }
}
