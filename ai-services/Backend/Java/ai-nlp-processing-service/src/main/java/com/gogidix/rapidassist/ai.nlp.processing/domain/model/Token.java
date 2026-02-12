package com.gogidix.rapidassist.ai.nlp.processing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Domain model representing a token from text tokenization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    private UUID id;
    private String tenantId;
    private UUID textProcessingId;
    private String text;
    private String lemma;
    private String partOfSpeech;
    private int position;
    private int startPosition;
    private int endPosition;
    private String morphology;
    private String dependency;
    private String headToken;

    public enum PartOfSpeech {
        NOUN,
        VERB,
        ADJECTIVE,
        ADVERB,
        PRONOUN,
        PREPOSITION,
        CONJUNCTION,
        DETERMINER,
        INTERJECTION,
        NUMERAL,
        PARTICLE,
        UNKNOWN
    }
}
