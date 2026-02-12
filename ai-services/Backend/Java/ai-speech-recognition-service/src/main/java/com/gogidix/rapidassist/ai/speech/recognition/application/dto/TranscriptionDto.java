package com.gogidix.rapidassist.ai.speech.recognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Transcription.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptionDto {

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
    private List<WordTimestampDto> wordTimestamps;

    // Language detection
    private String detectedLanguage;
    private Double languageConfidence;

    // Metadata
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
