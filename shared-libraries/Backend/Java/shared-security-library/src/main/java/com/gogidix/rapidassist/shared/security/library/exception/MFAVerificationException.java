package com.gogidix.rapidassist.shared.security.library.exception;

/**
 * Exception thrown when MFA validation fails.
 */
public class MFAVerificationException extends SecurityException {

    private final String mfaType;
    private final String userId;

    public MFAVerificationException(String message) {
        super("MFA_FAILED", message);
        this.mfaType = "UNKNOWN";
        this.userId = null;
    }

    public MFAVerificationException(String mfaType, String userId, String message) {
        super("MFA_FAILED", message);
        this.mfaType = mfaType;
        this.userId = userId;
    }

    public MFAVerificationException(String mfaType, String userId, String message, Throwable cause) {
        super("MFA_FAILED", message, cause);
        this.mfaType = mfaType;
        this.userId = userId;
    }

    public String getMfaType() {
        return mfaType;
    }

    public String getUserId() {
        return userId;
    }
}
