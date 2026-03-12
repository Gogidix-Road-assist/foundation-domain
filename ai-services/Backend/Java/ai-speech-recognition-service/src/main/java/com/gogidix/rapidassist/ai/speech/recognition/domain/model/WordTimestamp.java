package com.gogidix.rapidassist.ai.speech.recognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a Word Timestamp.
 * Pure domain model without persistence annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordTimestamp {

    private String word;
    private Double startTime;
    private Double endTime;
    private Double confidence;
    private Integer speakerIndex;
}
