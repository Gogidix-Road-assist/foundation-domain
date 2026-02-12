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
 * MongoDB Document for RateLimit.
 * Maps to rate_limit collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "rate_limit")
public class RateLimitEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String limitName;

    @Indexed(unique = true)
    private String identifier;

    @Indexed
    private com.gogidix.rapidassist.ai.gateway.domain.model.LimitType limitType;

    private Integer requestsPerWindow;
    private Integer windowSizeSeconds;
    private String algorithm;

    @Indexed
    private com.gogidix.rapidassist.ai.gateway.domain.model.RateLimitStatus status;

    @Indexed
    private String scope;

    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
