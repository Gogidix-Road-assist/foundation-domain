package com.gogidix.rapidassist.config.service.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "configurations")
public record Configuration(

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

    @Field("namespace")
    @NotBlank(message = "Namespace is required")
    String namespace,

    @Field("version")
    @NotNull(message = "Version is required")
    Integer version,

    @Field("value")
    Object value,

    @Field("dataType")
    @NotNull(message = "Data type is required")
    ConfigurationDataType dataType,

    @Field("encrypted")
    boolean encrypted,

    @Field("required")
    boolean required,

    @Field("defaultValue")
    Object defaultValue,

    @Field("description")
    String description,

    @Field("tags")
    Set<String> tags,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("schema")
    ConfigurationSchema schema,

    @Field("status")
    ConfigurationStatus status,

    @Field("createdBy")
    String createdBy,

    @Field("createdAt")
    Instant createdAt,

    @Field("updatedBy")
    String updatedBy,

    @Field("updatedAt")
    Instant updatedAt,

    @Field("lastValidatedAt")
    Instant lastValidatedAt,

    @Field("validationErrors")
    Set<String> validationErrors

) {

    public static Configuration create(
        String tenantId, String configKey, String environment,
        String namespace, Object value, ConfigurationDataType dataType,
        String createdBy
    ) {
        return new Configuration(
            null, tenantId, configKey, environment, namespace, 1, value,
            dataType, false, false, null, null, Set.of(), Map.of(),
            null, ConfigurationStatus.ACTIVE, createdBy, Instant.now(),
            createdBy, Instant.now(), null, Set.of()
        );
    }

    public Configuration withVersion(Integer newVersion) {
        return new Configuration(
            id, tenantId, configKey, environment, namespace, newVersion,
            value, dataType, encrypted, required, defaultValue, description,
            tags, metadata, schema, status, createdBy, createdAt,
            updatedBy, updatedAt, lastValidatedAt, validationErrors
        );
    }

    public Configuration withValue(Object newValue) {
        return new Configuration(
            id, tenantId, configKey, environment, namespace, version,
            newValue, dataType, encrypted, required, defaultValue, description,
            tags, metadata, schema, status, createdBy, createdAt,
            updatedBy, Instant.now(), lastValidatedAt, validationErrors
        );
    }

    public Configuration withStatus(ConfigurationStatus newStatus, String newUpdatedBy) {
        return new Configuration(
            id, tenantId, configKey, environment, namespace, version,
            value, dataType, encrypted, required, defaultValue, description,
            tags, metadata, schema, newStatus, createdBy, createdAt,
            newUpdatedBy, Instant.now(), lastValidatedAt, validationErrors
        );
    }

    public enum ConfigurationDataType {
        STRING, NUMBER, BOOLEAN, JSON, YAML, ARRAY, OBJECT, BINARY
    }

    public enum ConfigurationStatus {
        ACTIVE, INACTIVE, DEPRECATED, VALIDATION_FAILED, PENDING_APPROVAL
    }
}