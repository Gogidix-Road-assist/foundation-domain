package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.adapter.rest;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.SpeechRecognitionDto;
import com.gogidix.rapidassist.ai.speech.recognition.application.service.SpeechRecognitionApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Speech Recognition operations.
 * API endpoint: /api/v1/speech-recognition
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/speech-recognition")
@RequiredArgsConstructor
@Tag(name = "Speech Recognition", description = "Speech Recognition APIs")
public class SpeechRecognitionController {

    private final SpeechRecognitionApplicationService applicationService;

    /**
     * Create a new speech recognition request.
     * POST /api/v1/speech-recognition
     */
    @PostMapping
    @Operation(summary = "Create speech recognition request", description = "Creates a new speech recognition request for audio transcription")
    public ResponseEntity<SpeechRecognitionDto> createRecognitionRequest(
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId,
            @Parameter(description = "User ID", required = true) @RequestParam String userId,
            @Parameter(description = "Audio file path", required = true) @RequestParam String audioFilePath,
            @Parameter(description = "Language code", required = false) @RequestParam(defaultValue = "en-US") String language,
            @Parameter(description = "Recognition model", required = false) @RequestParam(defaultValue = "default") String model,
            @Parameter(description = "Enable punctuation", required = false) @RequestParam(defaultValue = "true") Boolean enablePunctuation,
            @Parameter(description = "Enable speaker diarization", required = false) @RequestParam(defaultValue = "false") Boolean enableSpeakerDiarization,
            @Parameter(description = "Enable word timestamps", required = false) @RequestParam(defaultValue = "false") Boolean enableWordTimestamps,
            @Parameter(description = "Maximum number of speakers", required = false) @RequestParam(defaultValue = "2") Integer maxSpeakers,
            @Parameter(description = "Additional metadata", required = false) @RequestParam(required = false) Map<String, Object> metadata) {

        log.info("Creating speech recognition request for tenant: {}, user: {}", tenantId, userId);

        SpeechRecognitionDto dto = applicationService.createRecognitionRequest(
                tenantId, userId, audioFilePath, language, model, enablePunctuation,
                enableSpeakerDiarization, enableWordTimestamps, maxSpeakers, metadata);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Get a speech recognition by ID.
     * GET /api/v1/speech-recognition/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get speech recognition", description = "Retrieves a speech recognition by ID")
    public ResponseEntity<SpeechRecognitionDto> getRecognition(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting speech recognition: {} for tenant: {}", id, tenantId);

        SpeechRecognitionDto dto = applicationService.getRecognitionById(id, tenantId);

        return ResponseEntity.ok(dto);
    }

    /**
     * Get a speech recognition by request ID.
     * GET /api/v1/speech-recognition/request/{requestId}
     */
    @GetMapping("/request/{requestId}")
    @Operation(summary = "Get speech recognition by request ID", description = "Retrieves a speech recognition by request ID")
    public ResponseEntity<SpeechRecognitionDto> getRecognitionByRequestId(
            @PathVariable String requestId,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting speech recognition by request ID: {} for tenant: {}", requestId, tenantId);

        SpeechRecognitionDto dto = applicationService.getRecognitionByRequestId(requestId, tenantId);

        return ResponseEntity.ok(dto);
    }

    /**
     * Get all speech recognition requests for a user.
     * GET /api/v1/speech-recognition/user/{userId}
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user speech recognitions", description = "Retrieves all speech recognition requests for a user")
    public ResponseEntity<List<SpeechRecognitionDto>> getUserRecognitions(
            @PathVariable String userId,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting speech recognition requests for user: {} in tenant: {}", userId, tenantId);

        List<SpeechRecognitionDto> dtos = applicationService.getRecognitionsByUser(userId, tenantId);

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get all speech recognition requests for a tenant.
     * GET /api/v1/speech-recognition/tenant/all
     */
    @GetMapping("/tenant/all")
    @Operation(summary = "Get all tenant speech recognitions", description = "Retrieves all speech recognition requests for a tenant")
    public ResponseEntity<List<SpeechRecognitionDto>> getAllRecognitions(
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting all speech recognition requests for tenant: {}", tenantId);

        List<SpeechRecognitionDto> dtos = applicationService.getAllRecognitions(tenantId);

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get speech recognition requests by status.
     * GET /api/v1/speech-recognition/status/{status}
     */
    @GetMapping("/status/{status}")
    @Operation(summary = "Get speech recognitions by status", description = "Retrieves speech recognition requests by status")
    public ResponseEntity<List<SpeechRecognitionDto>> getRecognitionsByStatus(
            @PathVariable String status,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting speech recognition requests with status: {} for tenant: {}", status, tenantId);

        List<SpeechRecognitionDto> dtos = applicationService.getRecognitionsByStatus(status, tenantId);

        return ResponseEntity.ok(dtos);
    }

    /**
     * Update transcription results.
     * PUT /api/v1/speech-recognition/{id}/transcription
     */
    @PutMapping("/{id}/transcription")
    @Operation(summary = "Update transcription", description = "Updates speech recognition with transcription results")
    public ResponseEntity<SpeechRecognitionDto> updateTranscription(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId,
            @Parameter(description = "Transcribed text", required = true) @RequestParam String transcription,
            @Parameter(description = "Confidence score", required = true) @RequestParam Double confidenceScore,
            @Parameter(description = "Recognition status", required = true) @RequestParam String status) {

        log.info("Updating transcription for speech recognition: {} in tenant: {}", id, tenantId);

        SpeechRecognitionDto dto = applicationService.updateTranscription(
                id, tenantId, transcription, confidenceScore, status);

        return ResponseEntity.ok(dto);
    }

    /**
     * Update speech recognition status.
     * PATCH /api/v1/speech-recognition/{id}/status
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update status", description = "Updates the status of a speech recognition request")
    public ResponseEntity<SpeechRecognitionDto> updateStatus(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId,
            @Parameter(description = "Recognition status", required = true) @RequestParam String status,
            @Parameter(description = "Processing status", required = false) @RequestParam(required = false) String processingStatus,
            @Parameter(description = "Error message", required = false) @RequestParam(required = false) String errorMessage) {

        log.info("Updating status for speech recognition: {} to status: {}", id, status);

        SpeechRecognitionDto dto = applicationService.updateStatus(
                id, tenantId, status, processingStatus, errorMessage);

        return ResponseEntity.ok(dto);
    }

    /**
     * Process speech recognition.
     * POST /api/v1/speech-recognition/{id}/process
     */
    @PostMapping("/{id}/process")
    @Operation(summary = "Process speech recognition", description = "Starts processing a speech recognition request")
    public ResponseEntity<SpeechRecognitionDto> processRecognition(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Processing speech recognition: {} for tenant: {}", id, tenantId);

        SpeechRecognitionDto dto = applicationService.processRecognition(id, tenantId);

        return ResponseEntity.ok(dto);
    }

    /**
     * Retry failed recognition request.
     * POST /api/v1/speech-recognition/{id}/retry
     */
    @PostMapping("/{id}/retry")
    @Operation(summary = "Retry recognition", description = "Retries a failed speech recognition request")
    public ResponseEntity<SpeechRecognitionDto> retryRecognition(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Retrying speech recognition: {} for tenant: {}", id, tenantId);

        SpeechRecognitionDto dto = applicationService.retryRecognition(id, tenantId);

        return ResponseEntity.ok(dto);
    }

    /**
     * Delete a speech recognition.
     * DELETE /api/v1/speech-recognition/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete speech recognition", description = "Deletes a speech recognition request")
    public ResponseEntity<Void> deleteRecognition(
            @PathVariable UUID id,
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Deleting speech recognition: {} for tenant: {}", id, tenantId);

        applicationService.deleteRecognition(id, tenantId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get pending recognition requests.
     * GET /api/v1/speech-recognition/admin/pending
     */
    @GetMapping("/admin/pending")
    @Operation(summary = "Get pending requests", description = "Retrieves all pending speech recognition requests for processing")
    public ResponseEntity<List<SpeechRecognitionDto>> getPendingRequests(
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting pending recognition requests for tenant: {}", tenantId);

        List<SpeechRecognitionDto> dtos = applicationService.getPendingRequests(tenantId);

        return ResponseEntity.ok(dtos);
    }

    /**
     * Get processing recognition requests.
     * GET /api/v1/speech-recognition/admin/processing
     */
    @GetMapping("/admin/processing")
    @Operation(summary = "Get processing requests", description = "Retrieves all processing speech recognition requests")
    public ResponseEntity<List<SpeechRecognitionDto>> getProcessingRequests(
            @RequestHeader("X-Tenant-ID") @NotBlank String tenantId) {

        log.info("Getting processing recognition requests for tenant: {}", tenantId);

        List<SpeechRecognitionDto> dtos = applicationService.getProcessingRequests(tenantId);

        return ResponseEntity.ok(dtos);
    }
}
