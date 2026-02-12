package com.gogidix.rapidassist.ai.nlp.processing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * DTO for tokenization results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenizationResultDto {
    private UUID textProcessingId;
    private List<TokenDto> tokens;
    private int tokenCount;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TokenDto {
    private String text;
    private String lemma;
    private String partOfSpeech;
    private int position;
    private int startPosition;
    private int endPosition;
}
