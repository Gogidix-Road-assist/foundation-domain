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
 * MongoDB Entity for Transcription.
 * Maps to transcription collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "transcription")
public class TranscriptionEntity {

    @Id
    private Long id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
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
    private List<WordTimestampEntity> wordTimestamps;

    // Language detection
    private String detectedLanguage;
    private Double languageConfidence;

    // Metadata
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
