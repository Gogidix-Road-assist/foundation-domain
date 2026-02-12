package com.gogidix.rapidassist.config.service.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "configuration_changes")
public record ConfigurationChange(

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
    ChangeType changeType,

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
    ApprovalStatus approvalStatus,

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

    public enum ChangeType {
        CREATE, UPDATE, DELETE, ROLLBACK, APPROVE, REJECT
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED, AUTO_APPROVED
    }

    public static ConfigurationChange create(
        String tenantId, String configKey, String environment,
        String namespace, ChangeType changeType, Integer previousVersion,
        Integer newVersion, Object previousValue, Object newValue,
        String changedBy, String reason
    ) {
        return new ConfigurationChange(
            null, tenantId, configKey, environment, namespace,
            java.util.UUID.randomUUID().toString(), changeType,
            previousVersion, newVersion, previousValue, newValue,
            changedBy, Instant.now(), reason, ApprovalStatus.AUTO_APPROVED,
            null, null, null, null, null, Map.of(), Set.of(), Set.of()
        );
    }

    public ConfigurationChange withApproval(ApprovalStatus status, String approvedBy) {
        return new ConfigurationChange(
            id, tenantId, configKey, environment, namespace,
            changeId, changeType, previousVersion, newVersion,
            previousValue, newValue, changedBy, changedAt, reason,
            status, approvedBy, status == ApprovalStatus.APPROVED || status == ApprovalStatus.REJECTED
                ? Instant.now() : null, rollbackVersion, rollbackBy, rollbackAt,
            metadata, affectedServices, validationErrors
        );
    }
}