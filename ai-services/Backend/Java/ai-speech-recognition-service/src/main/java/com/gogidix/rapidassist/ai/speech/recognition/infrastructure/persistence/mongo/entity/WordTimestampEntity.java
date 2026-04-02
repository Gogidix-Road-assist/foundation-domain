package com.gogidix.rapidassist.ai.speech.recognition.infrastructure.persistence.mongo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Embedded entity for WordTimestamp.
 * Stored within Transcription entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordTimestampEntity {

    private String word;
    private Double startTime;
    private Double endTime;
    private Double confidence;
    private Integer speakerIndex;
}
