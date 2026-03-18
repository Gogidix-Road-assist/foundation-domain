package com.gogidix.rapidassist.ai.computervision.domain.model;

/**
 * Enum representing confidence levels for detection results
 */
public enum ConfidenceLevel {
    VERY_HIGH(0.9, 1.0),
    HIGH(0.7, 0.9),
    MEDIUM(0.5, 0.7),
    LOW(0.3, 0.5),
    VERY_LOW(0.0, 0.3);

    private final double min;
    private final double max;

    ConfidenceLevel(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public static ConfidenceLevel fromScore(double score) {
        if (score >= 0.9) return VERY_HIGH;
        if (score >= 0.7) return HIGH;
        if (score >= 0.5) return MEDIUM;
        if (score >= 0.3) return LOW;
        return VERY_LOW;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
