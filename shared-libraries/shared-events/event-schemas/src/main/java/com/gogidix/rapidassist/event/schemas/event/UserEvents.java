package com.gogidix.rapidassist.event.schemas.event;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * User events for user lifecycle management.
 */
public class UserEvents {

    /**
     * Event fired when a new user is created
     */
    public static class UserCreated extends DomainEvent {
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String userType;
        private Map<String, Object> additionalInfo;

        public UserCreated() {
            super();
            this.eventType = "UserCreated";
            this.aggregateType = "User";
        }

        public UserCreated(String userId, String username, String email) {
            super("UserCreated", userId, "User");
            this.aggregateId = userId;
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public Map<String, Object> getAdditionalInfo() {
            return additionalInfo;
        }

        public void setAdditionalInfo(Map<String, Object> additionalInfo) {
            this.additionalInfo = additionalInfo;
        }
    }

    /**
     * Event fired when user information is updated
     */
    public static class UserUpdated extends DomainEvent {
        private String username;
        private Map<String, Object> changedFields;
        private String updatedBy;

        public UserUpdated() {
            super();
            this.eventType = "UserUpdated";
            this.aggregateType = "User";
        }

        public UserUpdated(String userId) {
            super("UserUpdated", userId, "User");
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public Map<String, Object> getChangedFields() {
            return changedFields;
        }

        public void setChangedFields(Map<String, Object> changedFields) {
            this.changedFields = changedFields;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }
    }

    /**
     * Event fired when a user is deleted
     */
    public static class UserDeleted extends DomainEvent {
        private String username;
        private String email;
        private String deletionReason;
        private Boolean permanent;

        public UserDeleted() {
            super();
            this.eventType = "UserDeleted";
            this.aggregateType = "User";
        }

        public UserDeleted(String userId) {
            super("UserDeleted", userId, "User");
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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
    }

    /**
     * Event fired when a user logs in
     */
    public static class UserLoggedIn extends DomainEvent {
        private String username;
        private String loginMethod;
        private String ipAddress;
        private String userAgent;
        private Boolean successful;
        private String failureReason;

        public UserLoggedIn() {
            super();
            this.eventType = "UserLoggedIn";
            this.aggregateType = "User";
        }

        public UserLoggedIn(String userId, String username) {
            super("UserLoggedIn", userId, "User");
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLoginMethod() {
            return loginMethod;
        }

        public void setLoginMethod(String loginMethod) {
            this.loginMethod = loginMethod;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getUserAgent() {
            return userAgent;
        }

        public void setUserAgent(String userAgent) {
            this.userAgent = userAgent;
        }

        public Boolean getSuccessful() {
            return successful;
        }

        public void setSuccessful(Boolean successful) {
            this.successful = successful;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public void setFailureReason(String failureReason) {
            this.failureReason = failureReason;
        }
    }

    /**
     * Event fired when a user logs out
     */
    public static class UserLoggedOut extends DomainEvent {
        private String username;
        private String sessionDuration;
        private String ipAddress;

        public UserLoggedOut() {
            super();
            this.eventType = "UserLoggedOut";
            this.aggregateType = "User";
        }

        public UserLoggedOut(String userId, String username) {
            super("UserLoggedOut", userId, "User");
            this.username = username;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSessionDuration() {
            return sessionDuration;
        }

        public void setSessionDuration(String sessionDuration) {
            this.sessionDuration = sessionDuration;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }

    /**
     * Event fired when user profile is viewed
     */
    public static class UserViewed extends DomainEvent {
        private String viewedUsername;
        private String viewerUsername;
        private String viewReason;

        public UserViewed() {
            super();
            this.eventType = "UserViewed";
            this.aggregateType = "User";
        }

        public String getViewedUsername() {
            return viewedUsername;
        }

        public void setViewedUsername(String viewedUsername) {
            this.viewedUsername = viewedUsername;
        }

        public String getViewerUsername() {
            return viewerUsername;
        }

        public void setViewerUsername(String viewerUsername) {
            this.viewerUsername = viewerUsername;
        }

        public String getViewReason() {
            return viewReason;
        }

        public void setViewReason(String viewReason) {
            this.viewReason = viewReason;
        }
    }
}
