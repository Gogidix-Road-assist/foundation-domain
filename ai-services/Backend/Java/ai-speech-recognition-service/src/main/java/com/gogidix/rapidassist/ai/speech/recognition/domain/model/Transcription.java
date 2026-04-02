package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Domain model representing a Transcription segment.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transcription {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID speechRecognitionId;

    // Transcription content
    private String text;
    private Integer segmentIndex;
    private Double startTime;
    private Double endTime;
    private Double confidence;

    // Speaker information
    private String speakerId;
    private String speakerLabel;
    private Double speakerConfidence;

    // Word-level timestamps
    private List<WordTimestamp> wordTimestamps;

    // Language detection
    private String detectedLanguage;
    private Double languageConfidence;

    // Metadata
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
