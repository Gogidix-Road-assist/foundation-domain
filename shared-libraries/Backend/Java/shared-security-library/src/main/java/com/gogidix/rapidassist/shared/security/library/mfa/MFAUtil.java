package com.gogidix.rapidassist.shared.security.library.mfa;

import com.gogidix.rapidassist.shared.security.library.exception.MFAVerificationException;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Utility class for Multi-Factor Authentication (MFA) operations.
 * Supports TOTP (Time-based One-Time Password) using Google Authenticator.
 */
@Component
public class MFAUtil {

    private final GoogleAuthenticator gAuth;
    private final SecureRandom secureRandom;

    public MFAUtil() {
        this.gAuth = new GoogleAuthenticator();
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate a new secret key for MFA
     * @return generated secret key
     */
    public String generateSecret() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    /**
     * Generate a QR code URL for Google Authenticator setup
     * @param issuerName name of the service/issuer
     * @param accountName username or account identifier
     * @param secret MFA secret key
     * @return QR code URL (can be used to generate QR image)
     */
    public String getQRCodeUrl(String issuerName, String accountName, String secret) {
        GoogleAuthenticatorKey key = new GoogleAuthenticatorKey.Builder(secret)
                .build();
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL(issuerName, accountName, key);
    }

    /**
     * Verify TOTP code
     * @param secret MFA secret key
     * @param code 6-digit code from authenticator app
     * @return true if code is valid
     */
    public Boolean verifyCode(String secret, Integer code) {
        if (code == null || code < 0 || code > 999999) {
            return false;
        }
        return gAuth.authorize(secret, code);
    }

    /**
     * Verify TOTP code with tolerance window (default 3)
     * @param secret MFA secret key
     * @param code 6-digit code from authenticator app
     * @param tolerance number of 30-second windows to check before/after current time
     * @return true if code is valid
     */
    public Boolean verifyCode(String secret, Integer code, Integer tolerance) {
        if (code == null || code < 0 || code > 999999) {
            return false;
        }
        return gAuth.authorize(secret, code, tolerance);
    }

    /**
     * Verify TOTP code and throw exception if invalid
     * @param secret MFA secret key
     * @param code 6-digit code from authenticator app
     * @param userId user ID for error reporting
     * @throws MFAVerificationException if code is invalid
     */
    public void verifyCodeOrThrow(String secret, Integer code, String userId) {
        if (!verifyCode(secret, code, 3)) {
            throw new MFAVerificationException("TOTP", userId, "Invalid MFA code");
        }
    }

    /**
     * Generate backup codes for account recovery
     * @param count number of backup codes to generate
     * @return array of backup codes
     */
    public String[] generateBackupCodes(Integer count) {
        String[] codes = new String[count];
        for (int i = 0; i < count; i++) {
            codes[i] = String.format("%06d", secureRandom.nextInt(1000000));
        }
        return codes;
    }

    /**
     * Verify backup code
     * @param backupCodes array of valid backup codes
     * @param code code to verify
     * @return true if code is a valid backup code
     */
    public Boolean verifyBackupCode(String[] backupCodes, String code) {
        for (String backupCode : backupCodes) {
            if (backupCode.equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate SMS verification code
     * @param length length of code (typically 6)
     * @return generated code
     */
    public String generateSmsCode(Integer length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Generate email verification code
     * @param length length of code (typically 6)
     * @return generated code
     */
    public String generateEmailCode(Integer length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }

    /**
     * Generate alphanumeric verification code
     * @param length length of code
     * @return generated code
     */
    public String generateAlphanumericCode(Integer length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // No ambiguous characters
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return code.toString();
    }
}
