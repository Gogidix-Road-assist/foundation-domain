package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a Speech Recognition request.
 * Pure domain model without persistence annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeechRecognition {

    private UUID id;
    private String tenantId;
    private String userId;
    private String requestId;

    // Audio information
    private String audioFilePath;
    private String audioFormat;
    private Long audioDurationMs;
    private Long audioFileSizeBytes;
    private Integer sampleRate;
    private Integer channels;

    // Recognition settings
    private String language;
    private String model;
    private Boolean enablePunctuation;
    private Boolean enableSpeakerDiarization;
    private Boolean enableWordTimestamps;
    private Integer maxSpeakers;

    // Recognition results
    private String transcription;
    private Double confidenceScore;
    private List<Transcription> transcriptions;
    private List<Speaker> speakers;

    // Processing information
    private String status;
    private String processingStatus;
    private String errorMessage;
    private Integer processingAttempts;
    private LocalDateTime processingStartedAt;
    private LocalDateTime processingCompletedAt;
    private Long processingDurationMs;

    // Metadata
    private java.util.Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
