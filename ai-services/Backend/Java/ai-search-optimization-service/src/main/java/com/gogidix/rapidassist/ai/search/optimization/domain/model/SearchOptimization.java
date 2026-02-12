package com.gogidix.rapidassist.ai.search.optimization.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing Search Optimization configuration.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchOptimization {

    private UUID id;
    private String tenantId;
    private String name;
    private String description;
    private String optimizationType;
    private Map<String, Object> configuration;
    private Double weight;
    private Boolean enabled;
    private Integer priority;
    private Map<String, Object> metadata;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Long version;
}
