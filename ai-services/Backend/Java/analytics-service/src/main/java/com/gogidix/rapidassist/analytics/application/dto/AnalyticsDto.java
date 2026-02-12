package com.gogidix.rapidassist.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Analytics operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {

    private UUID id;
    private String tenantId;
    private String analyticsType;
    private String dataSource;
    private Map<String, Object> metrics;
    private Map<String, Object> dimensions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime computedAt;
    private String status;
    private Integer totalRecords;
    private Double aggregationValue;
    private String aggregationType;
    private String computedBy;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
