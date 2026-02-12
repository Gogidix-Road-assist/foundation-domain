package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing types of anomalies that can be detected.
 */
public enum AnomalyType {

    STATISTICAL("Statistical", "Detected using statistical methods"),
    BEHAVIORAL("Behavioral", "Detected based on behavioral patterns"),
    PERFORMANCE("Performance", "Detected based on performance metrics"),
    SECURITY("Security", "Detected based on security indicators"),
    OPERATIONAL("Operational", "Detected in operational processes"),
    FINANCIAL("Financial", "Detected in financial transactions"),
    NETWORK("Network", "Detected in network traffic"),
    SYSTEM("System", "Detected in system operations"),
    DATA_QUALITY("Data Quality", "Detected in data quality checks"),
    CUSTOM("Custom", "Custom anomaly type");

    private final String code;
    private final String description;

    AnomalyType(String code, String description) {
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
