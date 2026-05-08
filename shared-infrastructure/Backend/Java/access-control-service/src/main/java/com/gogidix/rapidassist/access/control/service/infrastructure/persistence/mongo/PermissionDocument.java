package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB Document: PermissionDocument
 *
 * Document representation of Permission for MongoDB persistence.
 * Includes compound indexes for efficient tenant-scoped queries.
 */
@Document(collection = "permissions")
@CompoundIndex(name = "tenant_subject_idx", def = "{'tenantId': 1, 'subjectId': 1}")
@CompoundIndex(name = "tenant_resource_action_idx", def = "{'tenantId': 1, 'resource': 1, 'action': 1}")
public class PermissionDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String subjectId;

    private String subjectType; // USER, SERVICE, ROLE

    private String resource;
    private String action;
    private String effect; // ALLOW, DENY

    private Instant grantedAt;
    private String grantedBy;
    private Instant validUntil;
    private String condition;
    private boolean active;

    public PermissionDocument() {
    }

    public PermissionDocument(String id, String tenantId, String subjectId, String subjectType,
                             String resource, String action, String effect,
                             Instant grantedAt, String grantedBy, Instant validUntil,
                             String condition, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.subjectId = subjectId;
        this.subjectType = subjectType;
        this.resource = resource;
        this.action = action;
        this.effect = effect;
        this.grantedAt = grantedAt;
        this.grantedBy = grantedBy;
        this.validUntil = validUntil;
        this.condition = condition;
        this.active = active;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEffect() { return effect; }
    public void setEffect(String effect) { this.effect = effect; }

    public Instant getGrantedAt() { return grantedAt; }
    public void setGrantedAt(Instant grantedAt) { this.grantedAt = grantedAt; }

    public String getGrantedBy() { return grantedBy; }
    public void setGrantedBy(String grantedBy) { this.grantedBy = grantedBy; }

    public Instant getValidUntil() { return validUntil; }
    public void setValidUntil(Instant validUntil) { this.validUntil = validUntil; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
