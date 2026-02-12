package com.gogidix.rapidassist.api.gateway.domain.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Document(collection = "gateway_configurations")
public record GatewayConfiguration(

    @Id
    String id,

    @Field("tenantId")
    @NotBlank(message = "Tenant ID is required")
    String tenantId,

    @Field("configKey")
    @NotBlank(message = "Configuration key is required")
    String configKey,

    @Field("environment")
    @NotBlank(message = "Environment is required")
    String environment,

    @Field("configValue")
    Object configValue,

    @Field("configType")
    ConfigurationType configType,

    @Field("description")
    String description,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("createdBy")
    String createdBy,

    @Field("createdAt")
    Instant createdAt,

    @Field("updatedBy")
    String updatedBy,

    @Field("updatedAt")
    Instant updatedAt

) {

    public static GatewayConfiguration create(
        String tenantId, String configKey, String environment,
        Object configValue, ConfigurationType configType,
        String createdBy
    ) {
        return new GatewayConfiguration(
            null, tenantId, configKey, environment,
            configValue, configType, null, Map.of(),
            createdBy, Instant.now(), createdBy, Instant.now()
        );
    }

    public GatewayConfiguration withValue(Object newValue, String updatedBy) {
        return new GatewayConfiguration(
            id, tenantId, configKey, environment,
            newValue, configType, description, metadata,
            createdBy, createdAt, updatedBy, Instant.now()
        );
    }

    public enum ConfigurationType {
        GLOBAL, ROUTE, SECURITY, RATE_LIMIT, CIRCUIT_BREAKER, CACHE
    }
}
