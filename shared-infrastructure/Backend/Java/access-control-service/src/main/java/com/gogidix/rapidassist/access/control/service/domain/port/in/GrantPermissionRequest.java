package com.gogidix.rapidassist.access.control.service.domain.port.in;

import java.time.Instant;
import java.util.Objects;

/**
 * Request object for GrantPermissionCommand.
 * Encapsulates all parameters needed to grant a permission.
 */
public class GrantPermissionRequest {

    private final String tenantId;
    private final String subjectId;
    private final String subjectType;
    private final String resource;
    private final String action;
    private final String effect;
    private final String grantedBy;
    private final Instant validUntil;
    private final String condition;

    private GrantPermissionRequest(Builder builder) {
        this.tenantId = Objects.requireNonNull(builder.tenantId, "tenantId is required");
        this.subjectId = Objects.requireNonNull(builder.subjectId, "subjectId is required");
        this.subjectType = Objects.requireNonNull(builder.subjectType, "subjectType is required");
        this.resource = Objects.requireNonNull(builder.resource, "resource is required");
        this.action = Objects.requireNonNull(builder.action, "action is required");
        this.effect = Objects.requireNonNullElse(builder.effect, "ALLOW");
        this.grantedBy = builder.grantedBy;
        this.validUntil = builder.validUntil;
        this.condition = builder.condition;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTenantId() { return tenantId; }
    public String getSubjectId() { return subjectId; }
    public String getSubjectType() { return subjectType; }
    public String getResource() { return resource; }
    public String getAction() { return action; }
    public String getEffect() { return effect; }
    public String getGrantedBy() { return grantedBy; }
    public Instant getValidUntil() { return validUntil; }
    public String getCondition() { return condition; }

    public static class Builder {
        private String tenantId;
        private String subjectId;
        private String subjectType;
        private String resource;
        private String action;
        private String effect;
        private String grantedBy;
        private Instant validUntil;
        private String condition;

        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder subjectId(String subjectId) { this.subjectId = subjectId; return this; }
        public Builder subjectType(String subjectType) { this.subjectType = subjectType; return this; }
        public Builder resource(String resource) { this.resource = resource; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder effect(String effect) { this.effect = effect; return this; }
        public Builder grantedBy(String grantedBy) { this.grantedBy = grantedBy; return this; }
        public Builder validUntil(Instant validUntil) { this.validUntil = validUntil; return this; }
        public Builder condition(String condition) { this.condition = condition; return this; }

        public GrantPermissionRequest build() {
            return new GrantPermissionRequest(this);
        }
    }
}
