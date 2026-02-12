package com.gogidix.rapidassist.ai.speech.recognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Speaker.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerDto {

    private UUID id;
    private String tenantId;
    private UUID speechRecognitionId;

    // Speaker identification
    private String speakerId;
    private String speakerLabel;
    private Double confidence;

    // Speaker characteristics
    private String gender;
    private Double genderConfidence;
    private String ageGroup;
    private Double ageGroupConfidence;

    // Speaking statistics
    private Integer segmentCount;
    private Double totalSpeakingTime;
    private Double averageSpeakingTime;
    private Double firstStartTime;
    private Double lastEndTime;

    // Embeddings for speaker verification
    private String speakerEmbedding;

    // Metadata
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
