package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for ApiKey.
 * Maps to api_key collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "api_key")
public class ApiKeyEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String keyName;

    @Indexed(unique = true)
    private String apiKey;

    private String keyHash;

    @Indexed
    private com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus status;

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
    @Indexed
    private LocalDateTime lastUsedAt;
    private Long usageCount;
}
