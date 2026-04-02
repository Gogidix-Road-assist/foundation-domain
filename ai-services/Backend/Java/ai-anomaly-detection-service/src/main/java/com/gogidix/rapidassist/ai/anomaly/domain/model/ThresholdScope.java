package com.gogidix.rapidassist.ai.anomaly.domain.model;

/**
 * Enum representing threshold scope.
 */
public enum ThresholdScope {

    GLOBAL("Global", "Global threshold across all entities"),
    TENANT("Tenant", "Tenant-specific threshold"),
    ENTITY_TYPE("Entity Type", "Entity type specific threshold"),
    ENTITY("Entity", "Entity-specific threshold"),
    CUSTOM("Custom", "Custom scope");

    private final String code;
    private final String description;

    ThresholdScope(String code, String description) {
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
