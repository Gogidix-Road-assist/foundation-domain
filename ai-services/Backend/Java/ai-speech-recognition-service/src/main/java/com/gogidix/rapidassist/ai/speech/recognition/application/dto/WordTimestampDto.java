package com.gogidix.rapidassist.ai.speech.recognition.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for WordTimestamp.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordTimestampDto {

    private String word;
    private Double startTime;
    private Double endTime;
    private Double confidence;
    private Integer speakerIndex;
}
