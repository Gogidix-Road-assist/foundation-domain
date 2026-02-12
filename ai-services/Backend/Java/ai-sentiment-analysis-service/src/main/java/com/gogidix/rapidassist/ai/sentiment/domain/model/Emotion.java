package com.gogidix.rapidassist.ai.sentiment.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing an emotion detected in text
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emotion {

    private EmotionType emotionType;
    private Double confidence;
    private Double intensity;
    private String description;

    /**
     * Check if emotion is high confidence
     */
    public boolean isHighConfidence() {
        return confidence != null && confidence >= 0.7;
    }

    /**
     * Check if emotion is high intensity
     */
    public boolean isHighIntensity() {
        return intensity != null && intensity >= 0.7;
    }
}
