package com.gogidix.rapidassist.ai.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Domain model representing Gateway Configuration.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfig {

    private UUID id;
    private String tenantId;
    private String configName;
    private String description;
    private GatewayStatus status;
    private Map<String, Object> routingRules;
    private Map<String, Object> loadBalancingConfig;
    private Map<String, Object> rateLimitConfig;
    private Map<String, Object> authConfig;
    private Map<String, Object> monitoringConfig;
    private Integer priority;
    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
