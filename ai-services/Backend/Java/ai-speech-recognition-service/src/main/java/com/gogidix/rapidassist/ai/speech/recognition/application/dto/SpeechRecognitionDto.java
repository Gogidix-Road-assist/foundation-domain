package com.gogidix.rapidassist.ai.speech.recognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for SpeechRecognition.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeechRecognitionDto {

    @EqualsAndHashCode.Include


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
    private List<TranscriptionDto> transcriptions;
    private List<SpeakerDto> speakers;

    // Processing information
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
