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
 * MongoDB Document for GatewayConfig.
 * Maps to gateway_config collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "gateway_config")
public class GatewayConfigEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String configName;

    private String description;

    @Indexed
    private com.gogidix.rapidassist.ai.gateway.domain.model.GatewayStatus status;

    private Map<String, Object> routingRules;
    private Map<String, Object> loadBalancingConfig;
    private Map<String, Object> rateLimitConfig;
    private Map<String, Object> authConfig;
    private Map<String, Object> monitoringConfig;

    @Indexed
    private Integer priority;

    private Map<String, Object> metadata;
    private String version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
