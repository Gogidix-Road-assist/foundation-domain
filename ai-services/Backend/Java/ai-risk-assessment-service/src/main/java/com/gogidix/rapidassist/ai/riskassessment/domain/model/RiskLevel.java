package com.gogidix.rapidassist.ai.riskassessment.domain.model;

/**
 * Risk Level Enumeration
 * Defines the severity levels for risk assessments
 */
public enum RiskLevel {
    LOW(0.0, 30.0),
    MEDIUM(30.0, 60.0),
    HIGH(60.0, 80.0),
    CRITICAL(80.0, 100.0);

    private final double minScore;
    private final double maxScore;

    RiskLevel(double minScore, double maxScore) {
        this.minScore = minScore;
        this.maxScore = maxScore;
    }

    public double getMinScore() {
        return minScore;
    }

    public double getMaxScore() {
        return maxScore;
    }

    /**
     * Get risk level from score
     */
    public static RiskLevel fromScore(double score) {
        if (score < MEDIUM.minScore) return LOW;
        if (score < HIGH.minScore) return MEDIUM;
        if (score < CRITICAL.minScore) return HIGH;
        return CRITICAL;
    }

    /**
     * Check if score falls within this level
     */
    public boolean contains(double score) {
        return score >= minScore && score < maxScore;
    }
}
