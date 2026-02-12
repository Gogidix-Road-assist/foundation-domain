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
 * MongoDB Document for SearchResult.
 * Maps to search_result collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_result")
public class SearchResultEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private UUID queryId;

    @Indexed
    private String resultType;

    private String title;
    private String description;
    private String url;

    @Indexed
    private Double relevanceScore;

    @Indexed
    private Double rankingScore;

    @Indexed
    private Integer position;

    private Map<String, Object> attributes;
    private Map<String, Object> metadata;

    @Indexed
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
