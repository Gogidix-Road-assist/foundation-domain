package com.gogidix.rapidassist.mfa.service.infrastructure.provider.comprehensive;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

/**
 * Time-based One-Time Password (TOTP) implementation following RFC 6238.
 */
public class TotpMfaProvider {

    private static final Logger logger = LoggerFactory.getLogger(TotpMfaProvider.class);

    private static final int DEFAULT_TIME_STEP_SECONDS = 30;
    private static final int DEFAULT_CODE_DIGITS = 6;
    private static final String DEFAULT_ALGORITHM = "HmacSHA1";

    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a new TOTP secret
     */
    public String generateSecret() {
        byte[] buffer = new byte[20]; // 160 bits for SHA-1
        secureRandom.nextBytes(buffer);
        return Base64.getEncoder().withoutPadding().encodeToString(buffer);
    }

    /**
     * Generate a QR code URL for TOTP enrollment
     */
    public String generateQrUrl(String subject, String secret) {
        return String.format("otpauth://totp/%s?secret=%s&issuer=Gogidix",
                            subject.replace(":", "%3A"), secret);
    }

    /**
     * Verify a TOTP code
     */
    public MfaVerificationResult verify(String secret, String code) {
        try {
            // Allow for time drift (accept previous and next time step)
            for (int i = -1; i <= 1; i++) {
                String expectedCode = generateTotp(secret, i);
                if (expectedCode.equals(code)) {
                    logger.debug("TOTP verification successful");
                    return new MfaVerificationResult(true, null);
                }
            }

            return new MfaVerificationResult(false, "Invalid or expired verification code");

        } catch (Exception e) {
            logger.error("TOTP verification failed", e);
            return new MfaVerificationResult(false, "Verification error occurred");
        }
    }

    /**
     * Generate a TOTP code for the given secret and time step offset
     */
    private String generateTotp(String secret, int timeStepOffset) throws NoSuchAlgorithmException, InvalidKeyException {
        long timeStep = (Instant.now().getEpochSecond() / DEFAULT_TIME_STEP_SECONDS) + timeStepOffset;
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(timeStep).array();

        Mac mac = Mac.getInstance(DEFAULT_ALGORITHM);
        mac.init(new SecretKeySpec(secretBytes, DEFAULT_ALGORITHM));
        byte[] hash = mac.doFinal(data);

        // Dynamic truncation
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) |
                     ((hash[offset + 1] & 0xFF) << 16) |
                     ((hash[offset + 2] & 0xFF) << 8) |
                     (hash[offset + 3] & 0xFF);

        int code = binary % (int) Math.pow(10, DEFAULT_CODE_DIGITS);

        return String.format("%0" + DEFAULT_CODE_DIGITS + "d", code);
    }

    /**
     * Verify an HMAC-based One-Time Password (HOTP) code
     */
    public MfaVerificationResult verifyHotp(String secret, String code, long counter) {
        try {
            // Allow for some counter desynchronization
            for (long i = counter; i <= counter + 10; i++) {
                String expectedCode = generateHotp(secret, i);
                if (expectedCode.equals(code)) {
                    logger.debug("HOTP verification successful at counter: {}", i);
                    return new MfaVerificationResult(true, null);
                }
            }

            return new MfaVerificationResult(false, "Invalid verification code");

        } catch (Exception e) {
            logger.error("HOTP verification failed", e);
            return new MfaVerificationResult(false, "Verification error occurred");
        }
    }

    /**
     * Generate an HOTP code for the given secret and counter
     */
    private String generateHotp(String secret, long counter) throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretBytes = Base64.getDecoder().decode(secret);
        byte[] data = ByteBuffer.allocate(8).putLong(counter).array();

        Mac mac = Mac.getInstance(DEFAULT_ALGORITHM);
        mac.init(new SecretKeySpec(secretBytes, DEFAULT_ALGORITHM));
        byte[] hash = mac.doFinal(data);

        // Dynamic truncation
        int offset = hash[hash.length - 1] & 0x0F;
        int binary = ((hash[offset] & 0x7F) << 24) |
                     ((hash[offset + 1] & 0xFF) << 16) |
                     ((hash[offset + 2] & 0xFF) << 8) |
                     (hash[offset + 3] & 0xFF);

        int code = binary % (int) Math.pow(10, DEFAULT_CODE_DIGITS);

        return String.format("%0" + DEFAULT_CODE_DIGITS + "d", code);
    }
}