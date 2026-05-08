package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.Configuration;
import com.gogidix.rapidassist.config.service.domain.model.ConfigurationSchema;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "configurations")
public record ConfigurationDocument(

    @Id
    String id,

    @Field("tenantId")
    String tenantId,

    @Field("configKey")
    String configKey,

    @Field("environment")
    String environment,

    @Field("namespace")
    String namespace,

    @Field("version")
    Integer version,

    @Field("value")
    Object value,

    @Field("dataType")
    Configuration.ConfigurationDataType dataType,

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
    Configuration.ConfigurationStatus status,

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

    public static ConfigurationDocument fromDomain(Configuration config) {
        return new ConfigurationDocument(
            config.id(),
            config.tenantId(),
            config.configKey(),
            config.environment(),
            config.namespace(),
            config.version(),
            config.value(),
            config.dataType(),
            config.encrypted(),
            config.required(),
            config.defaultValue(),
            config.description(),
            config.tags(),
            config.metadata(),
            config.schema(),
            config.status(),
            config.createdBy(),
            config.createdAt(),
            config.updatedBy(),
            config.updatedAt(),
            config.lastValidatedAt(),
            config.validationErrors()
        );
    }

    public Configuration toDomain() {
        return new Configuration(
            id, tenantId, configKey, environment, namespace, version,
            value, dataType, encrypted, required, defaultValue, description,
            tags, metadata, schema, status, createdBy, createdAt,
            updatedBy, updatedAt, lastValidatedAt, validationErrors
        );
    }
}