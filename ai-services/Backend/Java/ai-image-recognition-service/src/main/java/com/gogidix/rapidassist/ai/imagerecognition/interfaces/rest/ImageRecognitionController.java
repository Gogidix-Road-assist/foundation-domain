package com.gogidix.rapidassist.ai.imagerecognition.interfaces.rest;

import com.gogidix.rapidassist.ai.imagerecognition.application.command.CreateImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.ProcessImageRecognitionCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.command.UpdateRecognitionStatusCommand;
import com.gogidix.rapidassist.ai.imagerecognition.application.dto.ImageRecognitionDto;
import com.gogidix.rapidassist.ai.imagerecognition.application.port.in.ImageRecognitionUseCase;
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
 * REST Controller for Image Recognition operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/ai/image-recognition")
@RequiredArgsConstructor
@Tag(name = "Image Recognition", description = "AI Image Recognition API")
public class ImageRecognitionController {

    private final ImageRecognitionUseCase imageRecognitionUseCase;

    @PostMapping
    @Operation(summary = "Create a new image recognition request")
    public ResponseEntity<ImageRecognitionDto> createRecognition(
            @Valid @RequestBody CreateImageRecognitionCommand command) {
        log.info("REST: Creating image recognition for tenant: {}, user: {}",
                 command.getTenantId(), command.getUserId());
        ImageRecognitionDto result = imageRecognitionUseCase.createRecognition(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get image recognition by ID")
    public ResponseEntity<ImageRecognitionDto> getRecognition(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Recognition ID") @PathVariable UUID id) {
        log.info("REST: Getting image recognition: {} for tenant: {}", id, tenantId);
        ImageRecognitionDto result = imageRecognitionUseCase.getRecognition(tenantId, id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/request/{requestId}")
    @Operation(summary = "Get image recognition by request ID")
    public ResponseEntity<ImageRecognitionDto> getRecognitionByRequestId(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Request ID") @PathVariable String requestId) {
        log.info("REST: Getting image recognition by request ID: {} for tenant: {}", requestId, tenantId);
        ImageRecognitionDto result = imageRecognitionUseCase.getRecognitionByRequestId(tenantId, requestId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    @Operation(summary = "Get all image recognitions for tenant")
    public ResponseEntity<List<ImageRecognitionDto>> getTenantRecognitions(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId) {
        log.info("REST: Getting all image recognitions for tenant: {}", tenantId);
        List<ImageRecognitionDto> results = imageRecognitionUseCase.getTenantRecognitions(tenantId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get image recognitions by user ID")
    public ResponseEntity<List<ImageRecognitionDto>> getUserRecognitions(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("REST: Getting image recognitions for user: {} in tenant: {}", userId, tenantId);
        List<ImageRecognitionDto> results = imageRecognitionUseCase.getUserRecognitions(tenantId, userId);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get image recognitions by status")
    public ResponseEntity<List<ImageRecognitionDto>> getRecognitionsByStatus(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Status") @PathVariable String status) {
        log.info("REST: Getting image recognitions by status: {} for tenant: {}", status, tenantId);
        List<ImageRecognitionDto> results = imageRecognitionUseCase.getRecognitionsByStatus(tenantId, status);
        return ResponseEntity.ok(results);
    }

    @PostMapping("/{id}/process")
    @Operation(summary = "Process image recognition")
    public ResponseEntity<ImageRecognitionDto> processRecognition(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Recognition ID") @PathVariable UUID id,
            @Valid @RequestBody ProcessImageRecognitionCommand command) {
        log.info("REST: Processing image recognition: {} for tenant: {}", id, tenantId);
        command.setTenantId(tenantId);
        command.setRecognitionId(id);
        ImageRecognitionDto result = imageRecognitionUseCase.processRecognition(command);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update recognition status")
    public ResponseEntity<ImageRecognitionDto> updateStatus(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Recognition ID") @PathVariable UUID id,
            @Valid @RequestBody UpdateRecognitionStatusCommand command) {
        log.info("REST: Updating status of image recognition: {} for tenant: {}", id, tenantId);
        command.setTenantId(tenantId);
        command.setRecognitionId(id);
        ImageRecognitionDto result = imageRecognitionUseCase.updateStatus(command);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete image recognition")
    public ResponseEntity<Void> deleteRecognition(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Recognition ID") @PathVariable UUID id) {
        log.info("REST: Deleting image recognition: {} for tenant: {}", id, tenantId);
        imageRecognitionUseCase.deleteRecognition(tenantId, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel image recognition")
    public ResponseEntity<ImageRecognitionDto> cancelRecognition(
            @Parameter(description = "Tenant ID") @RequestHeader("X-Tenant-ID") String tenantId,
            @Parameter(description = "Recognition ID") @PathVariable UUID id) {
        log.info("REST: Cancelling image recognition: {} for tenant: {}", id, tenantId);
        ImageRecognitionDto result = imageRecognitionUseCase.cancelRecognition(tenantId, id);
        return ResponseEntity.ok(result);
    }
}
