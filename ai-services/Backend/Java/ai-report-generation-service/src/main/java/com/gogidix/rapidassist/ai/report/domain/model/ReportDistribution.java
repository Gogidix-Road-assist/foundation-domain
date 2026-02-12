package com.gogidix.rapidassist.ai.report.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing a report distribution record.
 * Tracks how reports are distributed to various recipients.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportDistribution {

    private UUID id;
    private String tenantId;
    private UUID reportGenerationId;
    private DistributionType distributionType;
    private String destination;
    private Map<String, Object> distributionConfig;
    private boolean isSuccessful;
    private String responseMessage;
    private LocalDateTime distributedAt;
    private Integer retryCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
