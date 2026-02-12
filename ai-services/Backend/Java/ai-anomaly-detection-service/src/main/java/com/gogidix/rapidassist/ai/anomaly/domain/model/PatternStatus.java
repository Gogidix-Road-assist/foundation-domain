package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing pattern status.
 */
public enum PatternStatus {

    EMERGING("Emerging", "Pattern is emerging"),
    VALIDATED("Validated", "Pattern has been validated"),
    IGNORED("Ignored", "Pattern is ignored");

    private final String code;
    private final String description;

    PatternStatus(String code, String description) {
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
