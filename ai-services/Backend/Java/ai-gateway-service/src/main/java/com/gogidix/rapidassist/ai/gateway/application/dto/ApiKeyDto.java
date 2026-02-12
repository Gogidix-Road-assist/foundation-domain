package com.gogidix.rapidassist.ai.gateway.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for ApiKey.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDto {

    private UUID id;
    private String tenantId;
    private String keyName;
    private String apiKey;
    private String keyHash;
    private String status;
    private String[] allowedServices;
    private String[] allowedPaths;
    private Integer rateLimit;
    private Integer rateLimitWindowSeconds;
    private LocalDateTime expiresAt;
    private String createdBy;
    private String description;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    private Long usageCount;
}
