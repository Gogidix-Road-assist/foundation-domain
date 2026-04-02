package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Entity for Speaker.
 * Maps to speaker collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "speaker")
public class SpeakerEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID speechRecognitionId;

    // Speaker identification
    @Indexed
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
