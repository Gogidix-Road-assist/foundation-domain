package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.config.service.domain.model.ConfigurationChange;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "configuration_changes")
public record ConfigurationChangeDocument(

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

    @Field("changeId")
    String changeId,

    @Field("changeType")
    ConfigurationChange.ChangeType changeType,

    @Field("previousVersion")
    Integer previousVersion,

    @Field("newVersion")
    Integer newVersion,

    @Field("previousValue")
    Object previousValue,

    @Field("newValue")
    Object newValue,

    @Field("changedBy")
    String changedBy,

    @Field("changedAt")
    Instant changedAt,

    @Field("reason")
    String reason,

    @Field("approvalStatus")
    ConfigurationChange.ApprovalStatus approvalStatus,

    @Field("approvedBy")
    String approvedBy,

    @Field("approvedAt")
    Instant approvedAt,

    @Field("rollbackVersion")
    Integer rollbackVersion,

    @Field("rollbackBy")
    String rollbackBy,

    @Field("rollbackAt")
    Instant rollbackAt,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("affectedServices")
    Set<String> affectedServices,

    @Field("validationErrors")
    Set<String> validationErrors

) {

    public static ConfigurationChangeDocument fromDomain(ConfigurationChange change) {
        return new ConfigurationChangeDocument(
            change.id(),
            change.tenantId(),
            change.configKey(),
            change.environment(),
            change.namespace(),
            change.changeId(),
            change.changeType(),
            change.previousVersion(),
            change.newVersion(),
            change.previousValue(),
            change.newValue(),
            change.changedBy(),
            change.changedAt(),
            change.reason(),
            change.approvalStatus(),
            change.approvedBy(),
            change.approvedAt(),
            change.rollbackVersion(),
            change.rollbackBy(),
            change.rollbackAt(),
            change.metadata(),
            change.affectedServices(),
            change.validationErrors()
        );
    }

    public ConfigurationChange toDomain() {
        return new ConfigurationChange(
            id, tenantId, configKey, environment, namespace, changeId,
            changeType, previousVersion, newVersion, previousValue, newValue,
            changedBy, changedAt, reason, approvalStatus, approvedBy, approvedAt,
            rollbackVersion, rollbackBy, rollbackAt, metadata, affectedServices, validationErrors
        );
    }
}