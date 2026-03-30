package com.gogidix.rapidassist.event.schemas.event;

/**
 * Audit and compliance events.
 */
public class AuditEvents {

    /**
     * Event fired when data is accessed
     */
    public static class DataAccessed extends DomainEvent {
        private String entityType;
        private String entityId;
        private String accessType; // READ, EXPORT, VIEW
        private String accessReason;
        private String ipAddress;
        private String requestData;

        public DataAccessed() {
            super();
            this.eventType = "DataAccessed";
            this.aggregateType = "Audit";
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

        public String getAccessType() {
            return accessType;
        }

        public void setAccessType(String accessType) {
            this.accessType = accessType;
        }

        public String getAccessReason() {
            return accessReason;
        }

        public void setAccessReason(String accessReason) {
            this.accessReason = accessReason;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getRequestData() {
            return requestData;
        }

        public void setRequestData(String requestData) {
            this.requestData = requestData;
        }
    }

    /**
     * Event fired when data is modified
     */
    public static class DataModified extends DomainEvent {
        private String entityType;
        private String entityId;
        private String modificationType; // CREATE, UPDATE, DELETE
        private String changesJson;
        private String ipAddress;

        public DataModified() {
            super();
            this.eventType = "DataModified";
            this.aggregateType = "Audit";
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

        public String getModificationType() {
            return modificationType;
        }

        public void setModificationType(String modificationType) {
            this.modificationType = modificationType;
        }

        public String getChangesJson() {
            return changesJson;
        }

        public void setChangesJson(String changesJson) {
            this.changesJson = changesJson;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }

    /**
     * Event fired when data is deleted
     */
    public static class DataDeleted extends DomainEvent {
        private String entityType;
        private String entityId;
        private String entityName;
        private String deletionReason;
        private Boolean permanent;
        private String ipAddress;

        public DataDeleted() {
            super();
            this.eventType = "DataDeleted";
            this.aggregateType = "Audit";
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

        public String getDeletionReason() {
            return deletionReason;
        }

        public void setDeletionReason(String deletionReason) {
            this.deletionReason = deletionReason;
        }

        public Boolean getPermanent() {
            return permanent;
        }

        public void setPermanent(Boolean permanent) {
            this.permanent = permanent;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }

    /**
     * Event fired when permissions are changed
     */
    public static class PermissionChanged extends DomainEvent {
        private String targetUserId;
        private String targetUsername;
        private String permissionName;
        private String changeType; // GRANT, REVOKE
        private String changedBy;

        public PermissionChanged() {
            super();
            this.eventType = "PermissionChanged";
            this.aggregateType = "Permission";
        }

        public String getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(String targetUserId) {
            this.targetUserId = targetUserId;
        }

        public String getTargetUsername() {
            return targetUsername;
        }

        public void setTargetUsername(String targetUsername) {
            this.targetUsername = targetUsername;
        }

        public String getPermissionName() {
            return permissionName;
        }

        public void setPermissionName(String permissionName) {
            this.permissionName = permissionName;
        }

        public String getChangeType() {
            return changeType;
        }

        public void setChangeType(String changeType) {
            this.changeType = changeType;
        }

        public String getChangedBy() {
            return changedBy;
        }

        public void setChangedBy(String changedBy) {
            this.changedBy = changedBy;
        }
    }

    /**
     * Event fired when role is assigned or removed
     */
    public static class RoleChanged extends DomainEvent {
        private String targetUserId;
        private String targetUsername;
        private String roleName;
        private String changeType; // ASSIGN, UNASSIGN
        private String changedBy;

        public RoleChanged() {
            super();
            this.eventType = "RoleChanged";
            this.aggregateType = "Role";
        }

        public String getTargetUserId() {
            return targetUserId;
        }

        public void setTargetUserId(String targetUserId) {
            this.targetUserId = targetUserId;
        }

        public String getTargetUsername() {
            return targetUsername;
        }

        public void setTargetUsername(String targetUsername) {
            this.targetUsername = targetUsername;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

        public String getChangeType() {
            return changeType;
        }

        public void setChangeType(String changeType) {
            this.changeType = changeType;
        }

        public String getChangedBy() {
            return changedBy;
        }

        public void setChangedBy(String changedBy) {
            this.changedBy = changedBy;
        }
    }

    /**
     * Event fired for compliance violations
     */
    public static class ComplianceViolation extends DomainEvent {
        private String violationType;
        private String severity;
        private String description;
        private String policyViolated;
        private Boolean resolved;

        public ComplianceViolation() {
            super();
            this.eventType = "ComplianceViolation";
            this.aggregateType = "Compliance";
        }

        public String getViolationType() {
            return violationType;
        }

        public void setViolationType(String violationType) {
            this.violationType = violationType;
        }

        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {
            this.severity = severity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPolicyViolated() {
            return policyViolated;
        }

        public void setPolicyViolated(String policyViolated) {
            this.policyViolated = policyViolated;
        }

        public Boolean getResolved() {
            return resolved;
        }

        public void setResolved(Boolean resolved) {
            this.resolved = resolved;
        }
    }
}
