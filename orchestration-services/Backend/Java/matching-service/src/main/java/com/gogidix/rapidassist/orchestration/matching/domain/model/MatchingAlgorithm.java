package com.gogidix.rapidassist.orchestration.matching.domain.model;

import lombok.Getter;

/**
 * Enum defining the available matching algorithms
 */
@Getter
public enum MatchingAlgorithm {
    NEAREST("NEAREST", "Finds the geographically closest provider"),
    BEST_FIT("BEST_FIT", "Finds the best fit based on multiple factors"),
    LEAST_COST("LEAST_COST", "Finds the most cost-effective provider"),
    PRIORITY_BASED("PRIORITY_BASED", "Finds based on priority and availability");

    private final String code;
    private final String description;

    MatchingAlgorithm(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static MatchingAlgorithm fromCode(String code) {
        for (MatchingAlgorithm algorithm : values()) {
            if (algorithm.code.equalsIgnoreCase(code)) {
                return algorithm;
            }
        }
        throw new IllegalArgumentException("Unknown matching algorithm: " + code);
    }
}
