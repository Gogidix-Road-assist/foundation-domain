package com.gogidix.rapidassist.shared.audit.library.infrastructure.database;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * MongoDB document for persisting audit logs to the database.
 * This document stores all audit event information for compliance and reporting.
 */
@Document(collection = "audit_logs")
@CompoundIndex(name = "idx_audit_tenant_actor", def = "{'tenantId': 1, 'actorId': 1}")
@CompoundIndex(name = "idx_audit_tenant_entity", def = "{'tenantId': 1, 'entityType': 1, 'entityId': 1}")
@CompoundIndex(name = "idx_audit_tenant_date", def = "{'tenantId': 1, 'occurredAt': -1}")
public class AuditLogEntity {

    @Id
    private String id = UUID.randomUUID().toString();

    @Field("spec_version")
    @Indexed
    private String specVersion;

    @Field("event_id")
    @Indexed(unique = true)
    private String eventId;

    @Field("event_type")
    @Indexed
    private String eventType;

    @Field("occurred_at")
    @Indexed
    private Instant occurredAt;

    @Field("correlation_id")
    @Indexed
    private String correlationId;

    @Field("country")
    @Indexed
    private String country;

    @Field("tenant_id")
    @Indexed
    private String tenantId;

    @Field("sub_tenant_id")
    private String subTenantId;

    // Actor information
    @Field("actor_id")
    @Indexed
    private String actorId;

    @Field("actor_type")
    private String actorType;

    @Field("actor_name")
    private String actorName;

    // Entity reference
    @Field("entity_type")
    @Indexed
    private String entityType;

    @Field("entity_id")
    @Indexed
    private String entityId;

    @Field("entity_name")
    private String entityName;

    // Additional attributes stored as JSON
    @Field("attributes")
    private String attributes;

    // Payload stored as JSON
    @Field("payload")
    private String payload;

    @Field("created_at")
    @Indexed
    private Instant createdAt;

    // Constructors
    public AuditLogEntity() {
    }

    public AuditLogEntity(String specVersion, String eventId, String eventType, Instant occurredAt,
                          String correlationId, String country, String tenantId, String subTenantId,
                          String actorId, String actorType, String actorName,
                          String entityType, String entityId, String entityName,
                          String attributes, String payload) {
        this.specVersion = specVersion;
        this.eventId = eventId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.correlationId = correlationId;
        this.country = country;
        this.tenantId = tenantId;
        this.subTenantId = subTenantId;
        this.actorId = actorId;
        this.actorType = actorType;
        this.actorName = actorName;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityName = entityName;
        this.attributes = attributes;
        this.payload = payload;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSpecVersion() {
        return specVersion;
    }

    public void setSpecVersion(String specVersion) {
        this.specVersion = specVersion;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSubTenantId() {
        return subTenantId;
    }

    public void setSubTenantId(String subTenantId) {
        this.subTenantId = subTenantId;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorType() {
        return actorType;
    }

    public void setActorType(String actorType) {
        this.actorType = actorType;
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
