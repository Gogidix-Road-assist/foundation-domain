package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document("memberships")
public class MembershipDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String orgUnitId;

    @Indexed
    private String userId;

    private Set<String> roleIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getOrgUnitId() {
        return orgUnitId;
    }

    public void setOrgUnitId(String orgUnitId) {
        this.orgUnitId = orgUnitId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Set<String> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<String> roleIds) {
        this.roleIds = roleIds;
    }
}
