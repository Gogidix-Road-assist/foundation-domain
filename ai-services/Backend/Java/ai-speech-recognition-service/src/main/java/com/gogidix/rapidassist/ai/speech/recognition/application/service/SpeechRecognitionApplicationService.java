package com.gogidix.rapidassist.ai.speech.recognition.application.service;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.SpeechRecognitionDto;
import com.gogidix.rapidassist.ai.speech.recognition.application.mapper.SpeechRecognitionMapper;
import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.SpeechRecognitionRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.SpeechRecognition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application Service for Speech Recognition operations.
 * Implements business logic and orchestrates domain operations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpeechRecognitionApplicationService {

    private final SpeechRecognitionRepositoryPort repository;
    private final SpeechRecognitionMapper mapper;

    /**
     * Create a new speech recognition request.
     */
    public SpeechRecognitionDto createRecognitionRequest(String tenantId, String userId, String audioFilePath,
                                                          String language, String model, Boolean enablePunctuation,
                                                          Boolean enableSpeakerDiarization, Boolean enableWordTimestamps,
                                                          Integer maxSpeakers, java.util.Map<String, Object> metadata) {
        log.info("Creating speech recognition request for tenant: {}, user: {}, language: {}",
                 tenantId, userId, language);

        var speechRecognition = SpeechRecognition.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .audioFilePath(audioFilePath)
                .language(language)
                .model(model != null ? model : "default")
                .enablePunctuation(enablePunctuation != null ? enablePunctuation : true)
                .enableSpeakerDiarization(enableSpeakerDiarization != null ? enableSpeakerDiarization : false)
                .enableWordTimestamps(enableWordTimestamps != null ? enableWordTimestamps : false)
                .maxSpeakers(maxSpeakers != null ? maxSpeakers : 2)
                .status(RecognitionStatus.PENDING.name())
                .processingStatus("QUEUED")
                .processingAttempts(0)
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var savedRecognition = repository.save(speechRecognition);

        log.info("Speech recognition request created: {}", savedRecognition.getId());
        return mapper.toDto(savedRecognition);
    }

    /**
     * Get speech recognition by ID.
     */
    public SpeechRecognitionDto getRecognitionById(UUID id, String tenantId) {
        log.info("Getting speech recognition: {} for tenant: {}", id, tenantId);

        var recognition = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with ID: " + id + " for tenant: " + tenantId));

        return mapper.toDto(recognition);
    }

    /**
     * Get speech recognition by request ID.
     */
    public SpeechRecognitionDto getRecognitionByRequestId(String requestId, String tenantId) {
        log.info("Getting speech recognition by request ID: {} for tenant: {}", requestId, tenantId);

        var recognition = repository.findByRequestIdAndTenantId(requestId, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with request ID: " + requestId + " for tenant: " + tenantId));

        return mapper.toDto(recognition);
    }

    /**
     * Get all speech recognition requests for a user.
     */
    public List<SpeechRecognitionDto> getRecognitionsByUser(String userId, String tenantId) {
        log.info("Getting speech recognition requests for user: {} in tenant: {}", userId, tenantId);

        List<SpeechRecognition> recognitions = repository.findByUserIdAndTenantId(userId, tenantId);
        return mapper.toDtoList(recognitions);
    }

    /**
     * Get speech recognition requests by status.
     */
    public List<SpeechRecognitionDto> getRecognitionsByStatus(String status, String tenantId) {
        log.info("Getting speech recognition requests with status: {} for tenant: {}", status, tenantId);

        List<SpeechRecognition> recognitions = repository.findByStatusAndTenantId(status, tenantId);
        return mapper.toDtoList(recognitions);
    }

    /**
     * Get all speech recognition requests for a tenant.
     */
    public List<SpeechRecognitionDto> getAllRecognitions(String tenantId) {
        log.info("Getting all speech recognition requests for tenant: {}", tenantId);

        List<SpeechRecognition> recognitions = repository.findByTenantId(tenantId);
        return mapper.toDtoList(recognitions);
    }

    /**
     * Update speech recognition with transcription results.
     */
    public SpeechRecognitionDto updateTranscription(UUID id, String tenantId, String transcription,
                                                     Double confidenceScore, String status) {
        log.info("Updating transcription for speech recognition: {} in tenant: {}", id, tenantId);

        var recognition = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with ID: " + id + " for tenant: " + tenantId));

        recognition.setTranscription(transcription);
        recognition.setConfidenceScore(confidenceScore);
        recognition.setStatus(status);
        recognition.setProcessingStatus("COMPLETED");
        recognition.setProcessingCompletedAt(LocalDateTime.now());
        recognition.setUpdatedAt(LocalDateTime.now());

        if (recognition.getProcessingStartedAt() != null) {
            long duration = java.time.Duration.between(
                    recognition.getProcessingStartedAt(),
                    recognition.getProcessingCompletedAt()
            ).toMillis();
            recognition.setProcessingDurationMs(duration);
        }

        var savedRecognition = repository.save(recognition);

        log.info("Transcription updated for speech recognition: {}", savedRecognition.getId());
        return mapper.toDto(savedRecognition);
    }

    /**
     * Update speech recognition status.
     */
    public SpeechRecognitionDto updateStatus(UUID id, String tenantId, String status, String processingStatus,
                                              String errorMessage) {
        log.info("Updating status for speech recognition: {} to status: {}, processing: {}",
                 id, status, processingStatus);

        var recognition = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with ID: " + id + " for tenant: " + tenantId));

        recognition.setStatus(status);
        recognition.setProcessingStatus(processingStatus);

        if (errorMessage != null) {
            recognition.setErrorMessage(errorMessage);
        }

        if ("PROCESSING".equals(status) && recognition.getProcessingStartedAt() == null) {
            recognition.setProcessingStartedAt(LocalDateTime.now());
            recognition.setProcessingAttempts(recognition.getProcessingAttempts() + 1);
        }

        recognition.setUpdatedAt(LocalDateTime.now());

        var savedRecognition = repository.save(recognition);

        log.info("Status updated for speech recognition: {}", savedRecognition.getId());
        return mapper.toDto(savedRecognition);
    }

    /**
     * Delete speech recognition.
     */
    public void deleteRecognition(UUID id, String tenantId) {
        log.info("Deleting speech recognition: {} for tenant: {}", id, tenantId);

        if (!repository.existsByIdAndTenantId(id, tenantId)) {
            throw new RuntimeException(
                    "Speech recognition not found with ID: " + id + " for tenant: " + tenantId);
        }

        repository.deleteByIdAndTenantId(id, tenantId);

        log.info("Speech recognition deleted: {}", id);
    }

    /**
     * Process speech recognition (simulate AI processing).
     */
    public SpeechRecognitionDto processRecognition(UUID id, String tenantId) {
        log.info("Processing speech recognition: {} for tenant: {}", id, tenantId);

        var recognition = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with ID: " + id + " for tenant: " + tenantId));

        // Update status to processing
        recognition.setStatus(RecognitionStatus.PROCESSING.name());
        recognition.setProcessingStatus("IN_PROGRESS");
        recognition.setProcessingStartedAt(LocalDateTime.now());
        recognition.setProcessingAttempts(recognition.getProcessingAttempts() + 1);
        recognition.setUpdatedAt(LocalDateTime.now());

        var savedRecognition = repository.save(recognition);

        log.info("Speech recognition processing started: {}", savedRecognition.getId());
        return mapper.toDto(savedRecognition);
    }

    /**
     * Get pending recognition requests for processing.
     */
    public List<SpeechRecognitionDto> getPendingRequests(String tenantId) {
        log.info("Getting pending recognition requests for tenant: {}", tenantId);

        List<SpeechRecognition> recognitions = repository.findByStatusAndTenantId(
                RecognitionStatus.PENDING.name(), tenantId);

        return mapper.toDtoList(recognitions);
    }

    /**
     * Get processing recognition requests.
     */
    public List<SpeechRecognitionDto> getProcessingRequests(String tenantId) {
        log.info("Getting processing recognition requests for tenant: {}", tenantId);

        List<SpeechRecognition> recognitions = repository.findByStatusAndTenantId(
                RecognitionStatus.PROCESSING.name(), tenantId);

        return mapper.toDtoList(recognitions);
    }

    /**
     * Retry failed recognition request.
     */
    public SpeechRecognitionDto retryRecognition(UUID id, String tenantId) {
        log.info("Retrying failed speech recognition: {} for tenant: {}", id, tenantId);

        var recognition = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException(
                        "Speech recognition not found with ID: " + id + " for tenant: " + tenantId));

        recognition.setStatus(RecognitionStatus.PENDING.name());
        recognition.setProcessingStatus("QUEUED");
        recognition.setErrorMessage(null);
        recognition.setUpdatedAt(LocalDateTime.now());

        var savedRecognition = repository.save(recognition);

        log.info("Speech recognition queued for retry: {}", savedRecognition.getId());
        return mapper.toDto(savedRecognition);
    }
}
