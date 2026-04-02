package com.gogidix.rapidassist.ai.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing API Key.
 * Pure domain model without MongoDB annotations.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKey {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String keyName;
    private String apiKey;
    private String keyHash;
    private ApiKeyStatus status;
    private String[] allowedServices;
    private String[] allowedPaths;
    private Integer rateLimit;
    private Integer rateLimitWindowSeconds;
    private LocalDateTime expiresAt;
    private String createdBy;
    private String description;
    private java.util.Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastUsedAt;
    private Long usageCount;
}
