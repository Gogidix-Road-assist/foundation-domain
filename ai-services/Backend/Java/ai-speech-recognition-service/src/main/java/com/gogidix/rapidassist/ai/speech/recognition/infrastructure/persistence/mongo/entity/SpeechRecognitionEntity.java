package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for SpeechRecognition.
 * Maps to speech_recognition collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "speech_recognition")
public class SpeechRecognitionEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String requestId;

    // Audio information
    private String audioFilePath;
    private String audioFormat;
    private Long audioDurationMs;
    private Long audioFileSizeBytes;
    private Integer sampleRate;
    private Integer channels;

    // Recognition settings
    @Indexed
    private String language;
    private String model;
    private Boolean enablePunctuation;
    private Boolean enableSpeakerDiarization;
    private Boolean enableWordTimestamps;
    private Integer maxSpeakers;

    // Recognition results
    private String transcription;
    private Double confidenceScore;
    private List<TranscriptionEntity> transcriptions;
    private List<SpeakerEntity> speakers;

    // Processing information
    @Indexed
    private String status;
    private String processingStatus;
    private String errorMessage;
    private Integer processingAttempts;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private Long processingDurationMs;

    // Metadata
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
