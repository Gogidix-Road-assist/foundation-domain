package com.gogidix.rapidassist.ai.search.optimization.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for SearchQuery.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchQueryDto {

    private UUID id;
    private String tenantId;
    private String userId;
    private String originalQuery;
    private String optimizedQuery;
    private String queryIntent;
    private String queryType;
    private Map<String, Object> queryContext;
    private Map<String, Object> metadata;
    private Integer resultCount;
    private Double relevanceScore;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
