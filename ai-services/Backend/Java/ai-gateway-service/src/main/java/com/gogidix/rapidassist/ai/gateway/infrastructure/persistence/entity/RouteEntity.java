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
 * MongoDB Document for Route.
 * Maps to route collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "route")
public class RouteEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String routeName;

    @Indexed
    private String path;

    @Indexed
    private String serviceId;

    private String serviceUrl;
    private String httpMethod;

    @Indexed
    private com.gogidix.rapidassist.ai.gateway.domain.model.RouteStatus status;

    @Indexed
    private Integer priority;

    private Boolean isAuthenticated;
    private Boolean rateLimited;
    private Integer rateLimit;
    private Integer rateLimitWindowSeconds;
    private String circuitBreakerPolicy;
    private Integer timeoutMs;
    private Integer retryCount;
    private Map<String, String> headers;
    private Map<String, String> queryParams;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
