package com.gogidix.rapidassist.identity.access.service.infrastructure.mongo.document;

import com.gogidix.rapidassist.identity.access.service.domain.model.OrgUnitType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("org_units")
public class OrgUnitDocument {

    @Id
    private String id;

    @Indexed
    private String tenantId;

    @Indexed
    private String country;

    private OrgUnitType type;

    private String name;

    @Indexed
    private String parentId;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public OrgUnitType getType() {
        return type;
    }

    public void setType(OrgUnitType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
