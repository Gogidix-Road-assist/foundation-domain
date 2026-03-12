package com.gogidix.rapidassist.ai.search.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a Search Result.
 * Pure domain model without MongoDB annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private UUID queryId;
    private String resultType;
    private String title;
    private String description;
    private String url;
    private Double relevanceScore;
    private Double rankingScore;
    private Integer position;
    private Map<String, Object> attributes;
    private Map<String, Object> metadata;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
