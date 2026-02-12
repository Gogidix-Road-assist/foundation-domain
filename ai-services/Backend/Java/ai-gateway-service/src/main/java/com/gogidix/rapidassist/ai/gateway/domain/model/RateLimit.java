package com.gogidix.rapidassist.ai.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing Rate Limit configuration.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimit {

    private UUID id;
    private String tenantId;
    private String limitName;
    private String identifier;
    private LimitType limitType;
    private Integer requestsPerWindow;
    private Integer windowSizeSeconds;
    private String algorithm;
    private RateLimitStatus status;
    private String scope;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
