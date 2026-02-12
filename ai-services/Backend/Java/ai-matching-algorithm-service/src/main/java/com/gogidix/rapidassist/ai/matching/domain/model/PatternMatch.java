package com.gogidix.rapidassist.ai.matching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Pattern Match.
 * Identifies and extracts patterns from entity data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatternMatch {

    private UUID id;
    private String tenantId;
    private String patternId;
    private String patternName;
    private String patternType;
    private String entityType;
    private String entityId;
    private String patternExpression;
    private List<String> matchedValues;
    private Map<String, Object> matchedAttributes;
    private int matchCount;
    private double confidence;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private String createdBy;
}
