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
 * MongoDB Document for SearchOptimization.
 * Maps to search_optimization collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "search_optimization")
public class SearchOptimizationEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private String optimizationType;

    private Map<String, Object> configuration;

    @Indexed
    private Double weight;

    @Indexed
    private Boolean enabled;

    @Indexed
    private Integer priority;

    private Map<String, Object> metadata;

    @Indexed
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
