package com.gogidix.rapidassist.access.control.service.infrastructure.persistence.mongo;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * MongoDB Document: SubjectDocument
 *
 * Document representation of Subject for MongoDB persistence.
 */
@Document(collection = "subjects")
@CompoundIndex(name = "tenant_key_idx", def = "{'tenantId': 1, 'subjectKey': 1}", unique = true)
public class SubjectDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String subjectType; // USER, SERVICE, ROLE

    @Indexed
    private String subjectKey; // username, service name, role name

    private String displayName;
    private List<String> roleIds;
    private Instant createdAt;
    private Instant lastAccessAt;
    private boolean active;

    public SubjectDocument() {
    }

    public SubjectDocument(String id, String tenantId, String subjectType, String subjectKey,
                          String displayName, List<String> roleIds, Instant createdAt,
                          Instant lastAccessAt, boolean active) {
        this.id = id;
        this.tenantId = tenantId;
        this.subjectType = subjectType;
        this.subjectKey = subjectKey;
        this.displayName = displayName;
        this.roleIds = roleIds;
        this.createdAt = createdAt;
        this.lastAccessAt = lastAccessAt;
        this.active = active;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }

    public String getSubjectKey() { return subjectKey; }
    public void setSubjectKey(String subjectKey) { this.subjectKey = subjectKey; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public List<String> getRoleIds() { return roleIds; }
    public void setRoleIds(List<String> roleIds) { this.roleIds = roleIds; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastAccessAt() { return lastAccessAt; }
    public void setLastAccessAt(Instant lastAccessAt) { this.lastAccessAt = lastAccessAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
