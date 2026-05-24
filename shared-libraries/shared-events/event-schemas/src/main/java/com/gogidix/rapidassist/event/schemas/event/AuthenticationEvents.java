package com.gogidix.rapidassist.event.schemas.event;

/**
 * Authentication and authorization events.
 */
public class AuthenticationEvents {

    /**
     * Event fired on successful authentication
     */
    public static class AuthenticationSuccess extends DomainEvent {
        private String username;
        private String authMethod; // PASSWORD, OAUTH, SAML, MFA, API_KEY
        private String ipAddress;
        private String sessionId;
        private Boolean mfaVerified;

        public AuthenticationSuccess() {
            super();
            this.eventType = "AuthenticationSuccess";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAuthMethod() {
            return authMethod;
        }

        public void setAuthMethod(String authMethod) {
            this.authMethod = authMethod;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getSessionId() {
            return sessionId;
        }

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public Boolean getMfaVerified() {
            return mfaVerified;
        }

        public void setMfaVerified(Boolean mfaVerified) {
            this.mfaVerified = mfaVerified;
        }
    }

    /**
     * Event fired on failed authentication attempt
     */
    public static class AuthenticationFailure extends DomainEvent {
        private String username;
        private String authMethod;
        private String ipAddress;
        private String failureReason; // INVALID_CREDENTIALS, ACCOUNT_LOCKED, MFA_FAILED
        private Integer attemptNumber;
        private Boolean accountLocked;

        public AuthenticationFailure() {
            super();
            this.eventType = "AuthenticationFailure";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getAuthMethod() {
            return authMethod;
        }

        public void setAuthMethod(String authMethod) {
            this.authMethod = authMethod;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public void setFailureReason(String failureReason) {
            this.failureReason = failureReason;
        }

        public Integer getAttemptNumber() {
            return attemptNumber;
        }

        public void setAttemptNumber(Integer attemptNumber) {
            this.attemptNumber = attemptNumber;
        }

        public Boolean getAccountLocked() {
            return accountLocked;
        }

        public void setAccountLocked(Boolean accountLocked) {
            this.accountLocked = accountLocked;
        }
    }

    /**
     * Event fired when password is changed
     */
    public static class PasswordChanged extends DomainEvent {
        private String username;
        private String changeReason; // USER_INITIATED, ADMIN_RESET, EXPIRED, COMPROMISED
        private Boolean forcedChangeOnNextLogin;

        public PasswordChanged() {
            super();
            this.eventType = "PasswordChanged";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getChangeReason() {
            return changeReason;
        }

        public void setChangeReason(String changeReason) {
            this.changeReason = changeReason;
        }

        public Boolean getForcedChangeOnNextLogin() {
            return forcedChangeOnNextLogin;
        }

        public void setForcedChangeOnNextLogin(Boolean forcedChangeOnNextLogin) {
            this.forcedChangeOnNextLogin = forcedChangeOnNextLogin;
        }
    }

    /**
     * Event fired when MFA is enabled
     */
    public static class MFAEnabled extends DomainEvent {
        private String username;
        private String mfaMethod; // TOTP, SMS, EMAIL, HARDWARE_TOKEN

        public MFAEnabled() {
            super();
            this.eventType = "MFAEnabled";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getMfaMethod() {
            return mfaMethod;
        }

        public void setMfaMethod(String mfaMethod) {
            this.mfaMethod = mfaMethod;
        }
    }

    /**
     * Event fired when MFA is disabled
     */
    public static class MFADisabled extends DomainEvent {
        private String username;
        private String disabledBy;
        private String reason;

        public MFADisabled() {
            super();
            this.eventType = "MFADisabled";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getDisabledBy() {
            return disabledBy;
        }

        public void setDisabledBy(String disabledBy) {
            this.disabledBy = disabledBy;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    /**
     * Event fired when account is locked
     */
    public static class AccountLocked extends DomainEvent {
        private String username;
        private String lockReason;
        private String lockedUntil;

        public AccountLocked() {
            super();
            this.eventType = "AccountLocked";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLockReason() {
            return lockReason;
        }

        public void setLockReason(String lockReason) {
            this.lockReason = lockReason;
        }

        public String getLockedUntil() {
            return lockedUntil;
        }

        public void setLockedUntil(String lockedUntil) {
            this.lockedUntil = lockedUntil;
        }
    }

    /**
     * Event fired when account is unlocked
     */
    public static class AccountUnlocked extends DomainEvent {
        private String username;
        private String unlockedBy;
        private String unlockReason;

        public AccountUnlocked() {
            super();
            this.eventType = "AccountUnlocked";
            this.aggregateType = "Authentication";
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUnlockedBy() {
            return unlockedBy;
        }

        public void setUnlockedBy(String unlockedBy) {
            this.unlockedBy = unlockedBy;
        }

        public String getUnlockReason() {
            return unlockReason;
        }

        public void setUnlockReason(String unlockReason) {
            this.unlockReason = unlockReason;
        }
    }
}
