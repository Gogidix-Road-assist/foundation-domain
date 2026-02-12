package com.gogidix.rapidassist.ai.speech.recognition.application.service;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.SpeechRecognitionDto;
import com.gogidix.rapidassist.ai.speech.recognition.application.mapper.SpeechRecognitionMapper;
import com.gogidix.rapidassist.ai.speech.recognition.application.port.out.SpeechRecognitionRepositoryPort;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.SpeechRecognition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SpeechRecognitionApplicationService
 * Tests application layer business logic and use cases
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SpeechRecognitionApplicationService Tests")
class SpeechRecognitionApplicationServiceTest {

    @Mock
    private SpeechRecognitionRepositoryPort repository;

    @Mock
    private SpeechRecognitionMapper mapper;

    @InjectMocks
    private SpeechRecognitionApplicationService applicationService;

    private SpeechRecognition testRecognition;
    private SpeechRecognitionDto testDto;
    private UUID testId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        testRecognition = SpeechRecognition.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .audioFilePath("/audio/test.wav")
                .language("en-US")
                .model("default")
                .enablePunctuation(true)
                .enableSpeakerDiarization(false)
                .enableWordTimestamps(true)
                .maxSpeakers(2)
                .status(RecognitionStatus.COMPLETED.name())
                .processingStatus("COMPLETED")
                .transcription("Hello, world!")
                .confidenceScore(0.95)
                .processingAttempts(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testDto = SpeechRecognitionDto.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .requestId(testRecognition.getRequestId())
                .audioFilePath("/audio/test.wav")
                .language("en-US")
                .model("default")
                .enablePunctuation(true)
                .enableSpeakerDiarization(false)
                .enableWordTimestamps(true)
                .maxSpeakers(2)
                .status(RecognitionStatus.COMPLETED.name())
                .processingStatus("COMPLETED")
                .transcription("Hello, world!")
                .confidenceScore(0.95)
                .processingAttempts(1)
                .build();
    }

    @Test
    @DisplayName("Should create recognition request successfully")
    void testCreateRecognitionRequest() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "api");

        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.createRecognitionRequest(
                tenantId, userId, "/audio/test.wav", "en-US", "default",
                true, false, true, 2, metadata
        );

        assertNotNull(result);
        assertEquals(testId, result.getId());
        assertEquals(tenantId, result.getTenantId());
        assertEquals(userId, result.getUserId());

        verify(repository, times(1)).save(any(SpeechRecognition.class));
        verify(mapper, times(1)).toDto(testRecognition);
    }

    @Test
    @DisplayName("Should create recognition request with default values")
    void testCreateRecognitionRequestWithDefaults() {
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.createRecognitionRequest(
                tenantId, userId, "/audio/test.wav", "en-US", null,
                null, null, null, null, null
        );

        assertNotNull(result);
        verify(repository, times(1)).save(argThat(r ->
                "default".equals(r.getModel()) &&
                r.getEnablePunctuation() &&
                !r.getEnableSpeakerDiarization() &&
                !r.getEnableWordTimestamps() &&
                r.getMaxSpeakers() == 2
        ));
    }

    @Test
    @DisplayName("Should get recognition by ID")
    void testGetRecognitionById() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.getRecognitionById(testId, tenantId);

        assertNotNull(result);
        assertEquals(testId, result.getId());

        verify(repository, times(1)).findByIdAndTenantId(testId, tenantId);
        verify(mapper, times(1)).toDto(testRecognition);
    }

    @Test
    @DisplayName("Should throw exception when recognition not found by ID")
    void testGetRecognitionByIdNotFound() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.getRecognitionById(testId, tenantId);
        });

        verify(repository, times(1)).findByIdAndTenantId(testId, tenantId);
        verify(mapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Should get recognition by request ID")
    void testGetRecognitionByRequestId() {
        String requestId = testRecognition.getRequestId();

        when(repository.findByRequestIdAndTenantId(requestId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.getRecognitionByRequestId(requestId, tenantId);

        assertNotNull(result);
        assertEquals(requestId, result.getRequestId());

        verify(repository, times(1)).findByRequestIdAndTenantId(requestId, tenantId);
        verify(mapper, times(1)).toDto(testRecognition);
    }

    @Test
    @DisplayName("Should throw exception when recognition not found by request ID")
    void testGetRecognitionByRequestIdNotFound() {
        String requestId = "non-existent-request-id";

        when(repository.findByRequestIdAndTenantId(requestId, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.getRecognitionByRequestId(requestId, tenantId);
        });

        verify(repository, times(1)).findByRequestIdAndTenantId(requestId, tenantId);
    }

    @Test
    @DisplayName("Should get recognitions by user")
    void testGetRecognitionsByUser() {
        List<SpeechRecognition> recognitions = List.of(testRecognition);

        when(repository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(recognitions);
        when(mapper.toDtoList(recognitions)).thenReturn(List.of(testDto));

        List<SpeechRecognitionDto> results = applicationService.getRecognitionsByUser(userId, tenantId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(userId, results.get(0).getUserId());

        verify(repository, times(1)).findByUserIdAndTenantId(userId, tenantId);
        verify(mapper, times(1)).toDtoList(recognitions);
    }

    @Test
    @DisplayName("Should return empty list when no recognitions found for user")
    void testGetRecognitionsByUserEmpty() {
        when(repository.findByUserIdAndTenantId(userId, tenantId))
                .thenReturn(List.of());
        when(mapper.toDtoList(List.of())).thenReturn(List.of());

        List<SpeechRecognitionDto> results = applicationService.getRecognitionsByUser(userId, tenantId);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should get recognitions by status")
    void testGetRecognitionsByStatus() {
        String status = RecognitionStatus.COMPLETED.name();
        List<SpeechRecognition> recognitions = List.of(testRecognition);

        when(repository.findByStatusAndTenantId(status, tenantId))
                .thenReturn(recognitions);
        when(mapper.toDtoList(recognitions)).thenReturn(List.of(testDto));

        List<SpeechRecognitionDto> results = applicationService.getRecognitionsByStatus(status, tenantId);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(status, results.get(0).getStatus());

        verify(repository, times(1)).findByStatusAndTenantId(status, tenantId);
        verify(mapper, times(1)).toDtoList(recognitions);
    }

    @Test
    @DisplayName("Should get all recognitions for tenant")
    void testGetAllRecognitions() {
        List<SpeechRecognition> recognitions = Arrays.asList(
                testRecognition,
                createRecognition("id-2", tenantId, userId),
                createRecognition("id-3", tenantId, "other-user")
        );

        when(repository.findByTenantId(tenantId)).thenReturn(recognitions);
        when(mapper.toDtoList(recognitions)).thenReturn(Arrays.asList(
                testDto,
                testDto,
                testDto
        ));

        List<SpeechRecognitionDto> results = applicationService.getAllRecognitions(tenantId);

        assertNotNull(results);
        assertEquals(3, results.size());

        verify(repository, times(1)).findByTenantId(tenantId);
        verify(mapper, times(1)).toDtoList(recognitions);
    }

    @Test
    @DisplayName("Should update transcription successfully")
    void testUpdateTranscription() {
        String newTranscription = "Updated transcription text";
        Double newConfidence = 0.98;
        String newStatus = RecognitionStatus.COMPLETED.name();

        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.updateTranscription(
                testId, tenantId, newTranscription, newConfidence, newStatus
        );

        assertNotNull(result);
        verify(repository, times(1)).findByIdAndTenantId(testId, tenantId);
        verify(repository, times(1)).save(argThat(r ->
                newTranscription.equals(r.getTranscription()) &&
                newConfidence.equals(r.getConfidenceScore()) &&
                newStatus.equals(r.getStatus()) &&
                "COMPLETED".equals(r.getProcessingStatus()) &&
                r.getProcessingCompletedAt() != null &&
                r.getProcessingDurationMs() != null
        ));
    }

    @Test
    @DisplayName("Should throw exception when updating transcription for non-existent recognition")
    void testUpdateTranscriptionNotFound() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.updateTranscription(testId, tenantId, "text", 0.9, "COMPLETED");
        });

        verify(repository, times(1)).findByIdAndTenantId(testId, tenantId);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should update status successfully")
    void testUpdateStatus() {
        String newStatus = RecognitionStatus.PROCESSING.name();
        String newProcessingStatus = "IN_PROGRESS";
        String errorMessage = "Some error";

        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.updateStatus(
                testId, tenantId, newStatus, newProcessingStatus, errorMessage
        );

        assertNotNull(result);
        verify(repository, times(1)).save(argThat(r ->
                newStatus.equals(r.getStatus()) &&
                newProcessingStatus.equals(r.getProcessingStatus()) &&
                errorMessage.equals(r.getErrorMessage())
        ));
    }

    @Test
    @DisplayName("Should update status and set processing start time")
    void testUpdateStatusWithProcessingStart() {
        testRecognition.setProcessingStartedAt(null);

        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        applicationService.updateStatus(
                testId, tenantId, RecognitionStatus.PROCESSING.name(), "IN_PROGRESS", null
        );

        verify(repository, times(1)).save(argThat(r ->
                r.getProcessingStartedAt() != null &&
                r.getProcessingAttempts() != null &&
                r.getProcessingAttempts() > 0
        ));
    }

    @Test
    @DisplayName("Should delete recognition successfully")
    void testDeleteRecognition() {
        when(repository.existsByIdAndTenantId(testId, tenantId)).thenReturn(true);
        doNothing().when(repository).deleteByIdAndTenantId(testId, tenantId);

        applicationService.deleteRecognition(testId, tenantId);

        verify(repository, times(1)).existsByIdAndTenantId(testId, tenantId);
        verify(repository, times(1)).deleteByIdAndTenantId(testId, tenantId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent recognition")
    void testDeleteRecognitionNotFound() {
        when(repository.existsByIdAndTenantId(testId, tenantId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            applicationService.deleteRecognition(testId, tenantId);
        });

        verify(repository, times(1)).existsByIdAndTenantId(testId, tenantId);
        verify(repository, never()).deleteByIdAndTenantId(any(), any());
    }

    @Test
    @DisplayName("Should process recognition successfully")
    void testProcessRecognition() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.processRecognition(testId, tenantId);

        assertNotNull(result);
        verify(repository, times(1)).save(argThat(r ->
                RecognitionStatus.PROCESSING.name().equals(r.getStatus()) &&
                "IN_PROGRESS".equals(r.getProcessingStatus()) &&
                r.getProcessingStartedAt() != null &&
                r.getProcessingAttempts() != null &&
                r.getProcessingAttempts() > 0
        ));
    }

    @Test
    @DisplayName("Should get pending requests")
    void testGetPendingRequests() {
        List<SpeechRecognition> pendingRecognitions = List.of(
                createPendingRecognition(),
                createPendingRecognition()
        );

        when(repository.findByStatusAndTenantId(RecognitionStatus.PENDING.name(), tenantId))
                .thenReturn(pendingRecognitions);
        when(mapper.toDtoList(pendingRecognitions))
                .thenReturn(Arrays.asList(testDto, testDto));

        List<SpeechRecognitionDto> results = applicationService.getPendingRequests(tenantId);

        assertNotNull(results);
        assertEquals(2, results.size());

        verify(repository, times(1)).findByStatusAndTenantId(
                RecognitionStatus.PENDING.name(), tenantId
        );
    }

    @Test
    @DisplayName("Should get processing requests")
    void testGetProcessingRequests() {
        List<SpeechRecognition> processingRecognitions = List.of(
                createProcessingRecognition()
        );

        when(repository.findByStatusAndTenantId(RecognitionStatus.PROCESSING.name(), tenantId))
                .thenReturn(processingRecognitions);
        when(mapper.toDtoList(processingRecognitions))
                .thenReturn(List.of(testDto));

        List<SpeechRecognitionDto> results = applicationService.getProcessingRequests(tenantId);

        assertNotNull(results);
        assertEquals(1, results.size());

        verify(repository, times(1)).findByStatusAndTenantId(
                RecognitionStatus.PROCESSING.name(), tenantId
        );
    }

    @Test
    @DisplayName("Should retry failed recognition successfully")
    void testRetryRecognition() {
        testRecognition.setStatus(RecognitionStatus.FAILED.name());
        testRecognition.setErrorMessage("Processing failed");

        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        SpeechRecognitionDto result = applicationService.retryRecognition(testId, tenantId);

        assertNotNull(result);
        verify(repository, times(1)).save(argThat(r ->
                RecognitionStatus.PENDING.name().equals(r.getStatus()) &&
                "QUEUED".equals(r.getProcessingStatus()) &&
                r.getErrorMessage() == null
        ));
    }

    @Test
    @DisplayName("Should throw exception when retrying non-existent recognition")
    void testRetryRecognitionNotFound() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicationService.retryRecognition(testId, tenantId);
        });

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle update status without error message")
    void testUpdateStatusWithoutError() {
        when(repository.findByIdAndTenantId(testId, tenantId))
                .thenReturn(Optional.of(testRecognition));
        when(repository.save(any(SpeechRecognition.class))).thenReturn(testRecognition);
        when(mapper.toDto(testRecognition)).thenReturn(testDto);

        applicationService.updateStatus(
                testId, tenantId, RecognitionStatus.COMPLETED.name(), "COMPLETED", null
        );

        verify(repository, times(1)).save(argThat(r ->
                r.getErrorMessage() == null
        ));
    }

    private SpeechRecognition createRecognition(String id, String tenantId, String userId) {
        return SpeechRecognition.builder()
                .id(UUID.fromString(id.replace("id-", "00000000-0000-0000-0000-00000000")))
                .tenantId(tenantId)
                .userId(userId)
                .status(RecognitionStatus.COMPLETED.name())
                .build();
    }

    private SpeechRecognition createPendingRecognition() {
        return SpeechRecognition.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .status(RecognitionStatus.PENDING.name())
                .processingStatus("QUEUED")
                .build();
    }

    private SpeechRecognition createProcessingRecognition() {
        return SpeechRecognition.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .status(RecognitionStatus.PROCESSING.name())
                .processingStatus("IN_PROGRESS")
                .processingStartedAt(LocalDateTime.now())
                .build();
    }
}
