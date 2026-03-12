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
 * Domain model representing a Speaker in audio diarization.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Speaker {

    @EqualsAndHashCode.Include


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
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
