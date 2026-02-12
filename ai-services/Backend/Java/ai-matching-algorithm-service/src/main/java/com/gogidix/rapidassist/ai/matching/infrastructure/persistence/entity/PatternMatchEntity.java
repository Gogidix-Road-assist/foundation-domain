package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for PatternMatch.
 * Maps to pattern_match collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pattern_match")
public class PatternMatchEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String patternId;

    @Indexed
    private String patternName;

    @Indexed
    private String patternType;

    @Indexed
    private String entityType;

    @Indexed
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
