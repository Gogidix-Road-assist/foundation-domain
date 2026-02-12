package com.gogidix.rapidassist.event.schemas.event;

/**
 * Service lifecycle events.
 */
public class ServiceEvents {

    /**
     * Event fired when a service is created
     */
    public static class ServiceCreated extends DomainEvent {
        private String serviceName;
        private String serviceType;
        private String serviceCode;

        public ServiceCreated() {
            super();
            this.eventType = "ServiceCreated";
            this.aggregateType = "Service";
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getServiceCode() {
            return serviceCode;
        }

        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }
    }

    /**
     * Event fired when a service is updated
     */
    public static class ServiceUpdated extends DomainEvent {
        private String serviceName;
        private String updateType;

        public ServiceUpdated() {
            super();
            this.eventType = "ServiceUpdated";
            this.aggregateType = "Service";
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getUpdateType() {
            return updateType;
        }

        public void setUpdateType(String updateType) {
            this.updateType = updateType;
        }
    }

    /**
     * Event fired when a service is deleted
     */
    public static class ServiceDeleted extends DomainEvent {
        private String serviceName;
        private String deletionReason;

        public ServiceDeleted() {
            super();
            this.eventType = "ServiceDeleted";
            this.aggregateType = "Service";
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getDeletionReason() {
            return deletionReason;
        }

        public void setDeletionReason(String deletionReason) {
            this.deletionReason = deletionReason;
        }
    }

    /**
     * Event fired when service status changes
     */
    public static class ServiceStatusChanged extends DomainEvent {
        private String serviceName;
        private String oldStatus;
        private String newStatus;
        private String statusReason;

        public ServiceStatusChanged() {
            super();
            this.eventType = "ServiceStatusChanged";
            this.aggregateType = "Service";
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getOldStatus() {
            return oldStatus;
        }

        public void setOldStatus(String oldStatus) {
            this.oldStatus = oldStatus;
        }

        public String getNewStatus() {
            return newStatus;
        }

        public void setNewStatus(String newStatus) {
            this.newStatus = newStatus;
        }

        public String getStatusReason() {
            return statusReason;
        }

        public void setStatusReason(String statusReason) {
            this.statusReason = statusReason;
        }
    }
}
