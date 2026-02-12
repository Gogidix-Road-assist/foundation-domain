package com.gogidix.rapidassist.ai.chatbot.domain.model;

import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Domain model representing a detected intent in a user message.
 */
@Builder
public class Intent {

    private IntentType intentType;
    private Double confidenceScore;
    private String description;
    private List<String> entities;
    private List<String> suggestedActions;
    private Boolean requiresHumanIntervention;
    private Instant detectedAt;

    /**
     * Creates a high confidence intent
     */
    public static Intent highConfidence(IntentType type, String description) {
        return Intent.builder()
                .intentType(type)
                .confidenceScore(0.85)
                .description(description)
                .requiresHumanIntervention(false)
                .detectedAt(Instant.now())
                .build();
    }

    /**
     * Creates a low confidence intent that may require human intervention
     */
    public static Intent lowConfidence(IntentType type, String description) {
        return Intent.builder()
                .intentType(type)
                .confidenceScore(0.45)
                .description(description)
                .requiresHumanIntervention(true)
                .detectedAt(Instant.now())
                .build();
    }

    /**
     * Checks if intent is highly confident
     */
    public boolean isHighConfidence() {
        return confidenceScore != null && confidenceScore >= 0.7;
    }

    /**
     * Checks if intent needs human review
     */
    public boolean needsHumanReview() {
        return Boolean.TRUE.equals(requiresHumanIntervention) ||
               (confidenceScore != null && confidenceScore < 0.5);
    }

    /**
     * Checks if entities were extracted
     */
    public boolean hasEntities() {
        return entities != null && !entities.isEmpty();
    }

    /**
     * Gets the primary suggested action
     */
    public String getPrimaryAction() {
        return (suggestedActions != null && !suggestedActions.isEmpty())
                ? suggestedActions.get(0)
                : null;
    }
}
