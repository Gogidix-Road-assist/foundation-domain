package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing threshold types.
 */
public enum ThresholdType {

    MIN_MAX("Min-Max", "Minimum and maximum threshold"),
    UPPER("Upper", "Upper threshold only"),
    LOWER("Lower", "Lower threshold only"),
    STANDARD_DEVIATION("Standard Deviation", "Standard deviation based threshold"),
    PERCENTILE("Percentile", "Percentile based threshold"),
    CUSTOM("Custom", "Custom threshold type");

    private final String code;
    private final String description;

    ThresholdType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
