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
 * Domain model representing Search Suggestion/Autocomplete.
 * Pure domain model without MongoDB annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchSuggestion {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String prefix;
    private String suggestion;
    private Double score;
    private Long frequency;
    private String category;
    private Map<String, Object> context;
    private Map<String, Object> metadata;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
