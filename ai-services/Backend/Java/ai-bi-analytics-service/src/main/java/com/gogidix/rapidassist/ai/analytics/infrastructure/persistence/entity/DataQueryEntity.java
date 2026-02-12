package com.gogidix.rapidassist.ai.analytics.infrastructure.persistence.entity;

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

/**
 * MongoDB entity for DataQuery
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data_queries")
public class DataQueryEntity {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    private String query;

    @Indexed
    private String status;

    private String dataSource;

    private Map<String, Object> parameters;

    private Map<String, Object> filters;

    private List<String> groupBy;

    private List<String> orderBy;

    private Integer limit;

    private Integer offset;

    private String createdBy;

    private String updatedBy;

    @Indexed
    private LocalDateTime createdAt;

    @Indexed
    private LocalDateTime updatedAt;

    private LocalDateTime executedAt;

    private Long executionTimeMs;

    private Integer resultCount;

    private Map<String, Object> results;

    private String errorMessage;

    private Map<String, Object> metadata;

    private List<String> tags;

    @Indexed
    private Boolean isActive;

    private Boolean isCached;

    private LocalDateTime cacheExpiryAt;

    @Indexed
    private String category;
}
