package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SpeechRecognition domain model
 * Tests domain model properties, builders, and business rules
 */
@DisplayName("SpeechRecognition Domain Model Tests")
class SpeechRecognitionDomainTest {

    private SpeechRecognition speechRecognition;
    private UUID testId;
    private String tenantId;
    private String userId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        tenantId = "tenant-123";
        userId = "user-456";

        speechRecognition = SpeechRecognition.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .requestId(UUID.randomUUID().toString())
                .audioFilePath("/audio/sample.wav")
                .audioFormat("wav")
                .audioDurationMs(5000L)
                .audioFileSizeBytes(102400L)
                .sampleRate(16000)
                .channels(1)
                .language("en-US")
                .model("default")
                .enablePunctuation(true)
                .enableSpeakerDiarization(false)
                .enableWordTimestamps(true)
                .maxSpeakers(2)
                .transcription("Hello, this is a test.")
                .confidenceScore(0.95)
                .status(RecognitionStatus.COMPLETED.name())
                .processingStatus("COMPLETED")
                .processingAttempts(1)
                .processingStartedAt(LocalDateTime.now().minusSeconds(5))
                .processingCompletedAt(LocalDateTime.now())
                .processingDurationMs(5000L)
                .metadata(createMetadata())
                .version("1.0")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should build speech recognition correctly")
    void testBuilder() {
        assertNotNull(speechRecognition);
        assertEquals(testId, speechRecognition.getId());
        assertEquals(tenantId, speechRecognition.getTenantId());
        assertEquals(userId, speechRecognition.getUserId());
        assertEquals("/audio/sample.wav", speechRecognition.getAudioFilePath());
        assertEquals("wav", speechRecognition.getAudioFormat());
        assertEquals(5000L, speechRecognition.getAudioDurationMs());
        assertEquals(102400L, speechRecognition.getAudioFileSizeBytes());
        assertEquals(16000, speechRecognition.getSampleRate());
        assertEquals(1, speechRecognition.getChannels());
        assertEquals("en-US", speechRecognition.getLanguage());
        assertEquals("default", speechRecognition.getModel());
        assertTrue(speechRecognition.getEnablePunctuation());
        assertFalse(speechRecognition.getEnableSpeakerDiarization());
        assertTrue(speechRecognition.getEnableWordTimestamps());
        assertEquals(2, speechRecognition.getMaxSpeakers());
        assertEquals("Hello, this is a test.", speechRecognition.getTranscription());
        assertEquals(0.95, speechRecognition.getConfidenceScore());
        assertEquals(RecognitionStatus.COMPLETED.name(), speechRecognition.getStatus());
        assertEquals("COMPLETED", speechRecognition.getProcessingStatus());
        assertEquals(1, speechRecognition.getProcessingAttempts());
        assertEquals(5000L, speechRecognition.getProcessingDurationMs());
        assertNotNull(speechRecognition.getMetadata());
        assertEquals("1.0", speechRecognition.getVersion());
    }

    @Test
    @DisplayName("Should create speech recognition with all null fields")
    void testBuilderWithNulls() {
        SpeechRecognition recognition = SpeechRecognition.builder()
                .build();

        assertNotNull(recognition);
        assertNull(recognition.getId());
        assertNull(recognition.getTenantId());
        assertNull(recognition.getUserId());
        assertNull(recognition.getAudioFilePath());
        assertNull(recognition.getAudioFormat());
        assertNull(recognition.getAudioDurationMs());
        assertNull(recognition.getLanguage());
        assertNull(recognition.getModel());
        assertNull(recognition.getTranscription());
        assertNull(recognition.getConfidenceScore());
        assertNull(recognition.getStatus());
        assertNull(recognition.getProcessingStatus());
        assertNull(recognition.getProcessingAttempts());
        assertNull(recognition.getMetadata());
    }

    @Test
    @DisplayName("Should create speech recognition with minimal fields")
    void testBuilderWithMinimalFields() {
        SpeechRecognition recognition = SpeechRecognition.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .userId(userId)
                .status(RecognitionStatus.PENDING.name())
                .build();

        assertNotNull(recognition);
        assertNotNull(recognition.getId());
        assertEquals(tenantId, recognition.getTenantId());
        assertEquals(userId, recognition.getUserId());
        assertEquals(RecognitionStatus.PENDING.name(), recognition.getStatus());
    }

    @Test
    @DisplayName("Should handle transcription updates")
    void testTranscriptionUpdates() {
        speechRecognition.setTranscription("Updated transcription text.");
        assertEquals("Updated transcription text.", speechRecognition.getTranscription());

        speechRecognition.setTranscription(null);
        assertNull(speechRecognition.getTranscription());
    }

    @Test
    @DisplayName("Should handle confidence score updates")
    void testConfidenceScoreUpdates() {
        speechRecognition.setConfidenceScore(0.87);
        assertEquals(0.87, speechRecognition.getConfidenceScore());

        speechRecognition.setConfidenceScore(0.0);
        assertEquals(0.0, speechRecognition.getConfidenceScore());

        speechRecognition.setConfidenceScore(1.0);
        assertEquals(1.0, speechRecognition.getConfidenceScore());
    }

    @Test
    @DisplayName("Should handle status transitions")
    void testStatusTransitions() {
        speechRecognition.setStatus(RecognitionStatus.PENDING.name());
        assertEquals(RecognitionStatus.PENDING.name(), speechRecognition.getStatus());

        speechRecognition.setStatus(RecognitionStatus.PROCESSING.name());
        assertEquals(RecognitionStatus.PROCESSING.name(), speechRecognition.getStatus());

        speechRecognition.setStatus(RecognitionStatus.COMPLETED.name());
        assertEquals(RecognitionStatus.COMPLETED.name(), speechRecognition.getStatus());

        speechRecognition.setStatus(RecognitionStatus.FAILED.name());
        assertEquals(RecognitionStatus.FAILED.name(), speechRecognition.getStatus());
    }

    @Test
    @DisplayName("Should handle processing status updates")
    void testProcessingStatusUpdates() {
        speechRecognition.setProcessingStatus("QUEUED");
        assertEquals("QUEUED", speechRecognition.getProcessingStatus());

        speechRecognition.setProcessingStatus("IN_PROGRESS");
        assertEquals("IN_PROGRESS", speechRecognition.getProcessingStatus());

        speechRecognition.setProcessingStatus("COMPLETED");
        assertEquals("COMPLETED", speechRecognition.getProcessingStatus());

        speechRecognition.setProcessingStatus("FAILED");
        assertEquals("FAILED", speechRecognition.getProcessingStatus());
    }

    @Test
    @DisplayName("Should handle error messages")
    void testErrorMessages() {
        speechRecognition.setErrorMessage("Audio file corrupted");
        assertEquals("Audio file corrupted", speechRecognition.getErrorMessage());

        speechRecognition.setErrorMessage(null);
        assertNull(speechRecognition.getErrorMessage());

        speechRecognition.setErrorMessage("");
        assertEquals("", speechRecognition.getErrorMessage());
    }

    @Test
    @DisplayName("Should handle processing attempts")
    void testProcessingAttempts() {
        speechRecognition.setProcessingAttempts(0);
        assertEquals(0, speechRecognition.getProcessingAttempts());

        speechRecognition.setProcessingAttempts(1);
        assertEquals(1, speechRecognition.getProcessingAttempts());

        speechRecognition.setProcessingAttempts(5);
        assertEquals(5, speechRecognition.getProcessingAttempts());
    }

    @Test
    @DisplayName("Should handle processing timestamps")
    void testProcessingTimestamps() {
        LocalDateTime startedAt = LocalDateTime.now();
        LocalDateTime completedAt = startedAt.plusSeconds(10);

        speechRecognition.setProcessingStartedAt(startedAt);
        speechRecognition.setProcessingCompletedAt(completedAt);

        assertEquals(startedAt, speechRecognition.getProcessingStartedAt());
        assertEquals(completedAt, speechRecognition.getProcessingCompletedAt());
    }

    @Test
    @DisplayName("Should handle processing duration")
    void testProcessingDuration() {
        speechRecognition.setProcessingDurationMs(1000L);
        assertEquals(1000L, speechRecognition.getProcessingDurationMs());

        speechRecognition.setProcessingDurationMs(0L);
        assertEquals(0L, speechRecognition.getProcessingDurationMs());

        speechRecognition.setProcessingDurationMs(999999L);
        assertEquals(999999L, speechRecognition.getProcessingDurationMs());
    }

    @Test
    @DisplayName("Should handle metadata operations")
    void testMetadataOperations() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "mobile-app");
        metadata.put("version", "2.0");
        metadata.put("userId", "user-123");

        speechRecognition.setMetadata(metadata);

        assertNotNull(speechRecognition.getMetadata());
        assertEquals(3, speechRecognition.getMetadata().size());
        assertEquals("mobile-app", speechRecognition.getMetadata().get("source"));
        assertEquals("2.0", speechRecognition.getMetadata().get("version"));
        assertEquals("user-123", speechRecognition.getMetadata().get("userId"));

        speechRecognition.setMetadata(null);
        assertNull(speechRecognition.getMetadata());
    }

    @Test
    @DisplayName("Should handle transcriptions list")
    void testTranscriptionsList() {
        Transcription t1 = new Transcription();
        t1.setText("Hello world");

        Transcription t2 = new Transcription();
        t2.setText("Test speech");

        List<Transcription> transcriptions = Arrays.asList(t1, t2);
        speechRecognition.setTranscriptions(transcriptions);

        assertNotNull(speechRecognition.getTranscriptions());
        assertEquals(2, speechRecognition.getTranscriptions().size());
        assertEquals("Hello world", speechRecognition.getTranscriptions().get(0).getText());
        assertEquals("Test speech", speechRecognition.getTranscriptions().get(1).getText());
    }

    @Test
    @DisplayName("Should handle speakers list")
    void testSpeakersList() {
        Speaker speaker1 = new Speaker();
        speaker1.setSpeakerId("S1");
        speaker1.setGender("MALE");

        Speaker speaker2 = new Speaker();
        speaker2.setSpeakerId("S2");
        speaker2.setGender("FEMALE");

        List<Speaker> speakers = Arrays.asList(speaker1, speaker2);
        speechRecognition.setSpeakers(speakers);

        assertNotNull(speechRecognition.getSpeakers());
        assertEquals(2, speechRecognition.getSpeakers().size());
        assertEquals("S1", speechRecognition.getSpeakers().get(0).getSpeakerId());
        assertEquals("S2", speechRecognition.getSpeakers().get(1).getSpeakerId());
    }

    @Test
    @DisplayName("Should handle empty lists")
    void testEmptyLists() {
        speechRecognition.setTranscriptions(new ArrayList<>());
        speechRecognition.setSpeakers(new ArrayList<>());

        assertNotNull(speechRecognition.getTranscriptions());
        assertNotNull(speechRecognition.getSpeakers());
        assertTrue(speechRecognition.getTranscriptions().isEmpty());
        assertTrue(speechRecognition.getSpeakers().isEmpty());
    }

    @Test
    @DisplayName("Should handle audio properties")
    void testAudioProperties() {
        speechRecognition.setAudioFilePath("/new/path/audio.mp3");
        speechRecognition.setAudioFormat("mp3");
        speechRecognition.setAudioDurationMs(10000L);
        speechRecognition.setAudioFileSizeBytes(204800L);
        speechRecognition.setSampleRate(44100);
        speechRecognition.setChannels(2);

        assertEquals("/new/path/audio.mp3", speechRecognition.getAudioFilePath());
        assertEquals("mp3", speechRecognition.getAudioFormat());
        assertEquals(10000L, speechRecognition.getAudioDurationMs());
        assertEquals(204800L, speechRecognition.getAudioFileSizeBytes());
        assertEquals(44100, speechRecognition.getSampleRate());
        assertEquals(2, speechRecognition.getChannels());
    }

    @Test
    @DisplayName("Should handle recognition settings")
    void testRecognitionSettings() {
        speechRecognition.setLanguage("es-ES");
        speechRecognition.setModel("advanced");
        speechRecognition.setEnablePunctuation(false);
        speechRecognition.setEnableSpeakerDiarization(true);
        speechRecognition.setEnableWordTimestamps(false);
        speechRecognition.setMaxSpeakers(5);

        assertEquals("es-ES", speechRecognition.getLanguage());
        assertEquals("advanced", speechRecognition.getModel());
        assertFalse(speechRecognition.getEnablePunctuation());
        assertTrue(speechRecognition.getEnableSpeakerDiarization());
        assertFalse(speechRecognition.getEnableWordTimestamps());
        assertEquals(5, speechRecognition.getMaxSpeakers());
    }

    @Test
    @DisplayName("Should handle audit fields")
    void testAuditFields() {
        LocalDateTime now = LocalDateTime.now();

        speechRecognition.setCreatedAt(now);
        speechRecognition.setUpdatedAt(now.plusMinutes(5));
        speechRecognition.setCreatedBy("system");
        speechRecognition.setUpdatedBy("admin");

        assertEquals(now, speechRecognition.getCreatedAt());
        assertEquals(now.plusMinutes(5), speechRecognition.getUpdatedAt());
        assertEquals("system", speechRecognition.getCreatedBy());
        assertEquals("admin", speechRecognition.getUpdatedBy());
    }

    @Test
    @DisplayName("Should handle version")
    void testVersion() {
        speechRecognition.setVersion("2.1.0");
        assertEquals("2.1.0", speechRecognition.getVersion());

        speechRecognition.setVersion(null);
        assertNull(speechRecognition.getVersion());

        speechRecognition.setVersion("");
        assertEquals("", speechRecognition.getVersion());
    }

    @Test
    @DisplayName("Should handle no-args constructor")
    void testNoArgsConstructor() {
        SpeechRecognition recognition = new SpeechRecognition();

        assertNotNull(recognition);
        assertNull(recognition.getId());
        assertNull(recognition.getTenantId());
        assertNull(recognition.getUserId());
    }

    @Test
    @DisplayName("Should handle all-args constructor")
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> metadata = createMetadata();

        SpeechRecognition recognition = new SpeechRecognition(
                testId, tenantId, userId, "req-123",
                "/audio/test.wav", "wav", 5000L, 102400L, 16000, 1,
                "en-US", "default", true, false, true, 2,
                "Test transcription", 0.95, null, null,
                RecognitionStatus.COMPLETED.name(), "COMPLETED", "No error",
                1, now.minusSeconds(5), now, 5000L,
                metadata, "1.0", now, now, "system", "admin"
        );

        assertNotNull(recognition);
        assertEquals(testId, recognition.getId());
        assertEquals(tenantId, recognition.getTenantId());
        assertEquals(userId, recognition.getUserId());
        assertEquals("req-123", recognition.getRequestId());
        assertEquals("/audio/test.wav", recognition.getAudioFilePath());
        assertEquals("Test transcription", recognition.getTranscription());
        assertEquals(0.95, recognition.getConfidenceScore());
    }

    @Test
    @DisplayName("Should handle toString")
    void testToString() {
        String str = speechRecognition.toString();

        assertNotNull(str);
        assertTrue(str.contains("SpeechRecognition"));
        assertTrue(str.contains(testId.toString()));
    }

    @Test
    @DisplayName("Should handle equals and hashCode")
    void testEqualsAndHashCode() {
        SpeechRecognition recognition1 = SpeechRecognition.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .build();

        SpeechRecognition recognition2 = SpeechRecognition.builder()
                .id(testId)
                .tenantId(tenantId)
                .userId(userId)
                .build();

        assertEquals(recognition1, recognition2);
        assertEquals(recognition1.hashCode(), recognition2.hashCode());
    }

    @Test
    @DisplayName("Should handle complex metadata")
    void testComplexMetadata() {
        Map<String, Object> complexMetadata = new HashMap<>();
        complexMetadata.put("string", "value");
        complexMetadata.put("number", 123);
        complexMetadata.put("boolean", true);
        complexMetadata.put("nested", Map.of("key", "value"));
        complexMetadata.put("list", Arrays.asList("item1", "item2"));

        speechRecognition.setMetadata(complexMetadata);

        assertNotNull(speechRecognition.getMetadata());
        assertEquals(5, speechRecognition.getMetadata().size());
        assertEquals("value", speechRecognition.getMetadata().get("string"));
        assertEquals(123, speechRecognition.getMetadata().get("number"));
        assertEquals(true, speechRecognition.getMetadata().get("boolean"));
    }

    private Map<String, Object> createMetadata() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", "api");
        metadata.put("environment", "production");
        return metadata;
    }
}
