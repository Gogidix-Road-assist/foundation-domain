package com.gogidix.rapidassist.analytics.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for AnalyticsCache operations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsCacheDto {

    private UUID id;
    private String tenantId;
    private String cacheKey;
    private String cacheType;
    private Object cachedData;
    private Map<String, Object> queryParameters;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastAccessedAt;
    private Integer accessCount;
    private Long sizeInBytes;
    private String status;
    private String createdBy;
    private Integer ttlSeconds;
    private Map<String, Object> metadata;
    private LocalDateTime updatedAt;
}
