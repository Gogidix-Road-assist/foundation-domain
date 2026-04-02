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
 * MongoDB Document for SearchSuggestion.
 * Maps to search_suggestion collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_suggestion")
public class SearchSuggestionEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String prefix;

    @Indexed
    private String suggestion;

    @Indexed
    private Double score;

    @Indexed
    private Long frequency;

    @Indexed
    private String category;

    private Map<String, Object> context;
    private Map<String, Object> metadata;

    @Indexed
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
