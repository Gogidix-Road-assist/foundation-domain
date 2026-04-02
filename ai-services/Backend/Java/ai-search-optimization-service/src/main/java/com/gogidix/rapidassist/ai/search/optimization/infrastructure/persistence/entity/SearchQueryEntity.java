package com.gogidix.rapidassist.ai.search.optimization.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for SearchQuery.
 * Maps to search_query collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_query")
public class SearchQueryEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String userId;

    @Indexed
    private String originalQuery;

    private String optimizedQuery;

    @Indexed
    private String queryIntent;

    @Indexed
    private String queryType;

    private Map<String, Object> queryContext;
    private Map<String, Object> metadata;

    private Integer resultCount;
    private Double relevanceScore;

    @Indexed
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
