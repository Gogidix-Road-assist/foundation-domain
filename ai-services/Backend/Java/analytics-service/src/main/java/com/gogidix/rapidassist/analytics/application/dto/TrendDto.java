package com.gogidix.rapidassist.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Trend operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendDto {

    private UUID id;
    private String tenantId;
    private String trendName;
    private String metricName;
    private String trendType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private List<Map<String, Object>> dataPoints;
    private String trendDirection;
    private Double trendStrength;
    private Double predictedValue;
    private Double confidenceInterval;
    private String seasonality;
    private Map<String, Object> trendParameters;
    private String status;
    private String description;
    private String algorithm;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
