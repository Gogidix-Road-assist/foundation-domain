package com.gogidix.rapidassist.ai.analytics.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a saved data query
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataQuery {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private String query;
    private QueryStatus status;
    private String dataSource;
    private Map<String, Object> parameters;
    private Map<String, Object> filters;
    private List<String> groupBy;
    private List<String> orderBy;
    private Integer limit;
    private Integer offset;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime executedAt;
    private Long executionTimeMs;
    private Integer resultCount;
    private Map<String, Object> results;
    private String errorMessage;
    private Map<String, Object> metadata;
    private List<String> tags;
    private Boolean isActive;
    private Boolean isCached;
    private LocalDateTime cacheExpiryAt;
    private String category;
}
