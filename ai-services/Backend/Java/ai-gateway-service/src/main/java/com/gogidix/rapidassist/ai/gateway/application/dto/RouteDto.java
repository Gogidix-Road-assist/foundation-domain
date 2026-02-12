package com.gogidix.rapidassist.ai.gateway.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for Route.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteDto {

    private UUID id;
    private String tenantId;
    private String routeName;
    private String path;
    private String serviceId;
    private String serviceUrl;
    private String httpMethod;
    private String status;
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
