package com.gogidix.rapidassist.mfa.service.infrastructure.provider.comprehensive;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.infrastructure.provider.MfaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * OTP delivery provider for SMS and Email verification codes.
 * In production, this would integrate with actual SMS (Twilio, AWS SNS)
 * and email (SendGrid, AWS SES) providers.
 */
public class OtpDeliveryProvider {

    private static final Logger logger = LoggerFactory.getLogger(OtpDeliveryProvider.class);

    private final MfaProperties properties;
    private final SecureRandom secureRandom;

    // In production, these would be replaced with actual provider integrations
    private final Map<String, OtpSession> activeOtps = new HashMap<>();

    public OtpDeliveryProvider(MfaProperties properties) {
        this.properties = properties;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate and send an OTP code
     */
    public String generateAndSend(String enrollmentId, String subject, String method) {
        String code = generateOtpCode();
        String deliveryAddress = subject; // In production, this would be phone/email from user profile

        try {
            switch (method.toUpperCase()) {
                case "SMS":
                    sendSmsOtp(deliveryAddress, code);
                    break;
                case "EMAIL":
                    sendEmailOtp(deliveryAddress, code);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported delivery method: " + method);
            }

            // Store OTP session for verification
            OtpSession session = new OtpSession(code, Instant.now(), Instant.now().plusSeconds(properties.getOtp().getExpirationSeconds()));
            activeOtps.put(enrollmentId, session);

            logger.info("OTP sent via {} to enrollment: {}", method, enrollmentId);
            return code;

        } catch (Exception e) {
            logger.error("Failed to send OTP via {}", method, e);
            throw new RuntimeException("OTP delivery failed", e);
        }
    }

    /**
     * Verify an OTP code
     */
    public MfaVerificationResult verify(String enrollmentId, String code) {
        OtpSession session = activeOtps.get(enrollmentId);

        if (session == null) {
            return new MfaVerificationResult(false, "No active OTP session found");
        }

        if (session.expiresAt.isBefore(Instant.now())) {
            activeOtps.remove(enrollmentId);
            return new MfaVerificationResult(false, "OTP has expired");
        }

        if (session.attempts >= properties.getOtp().getMaxAttempts()) {
            activeOtps.remove(enrollmentId);
            return new MfaVerificationResult(false, "Maximum attempts exceeded");
        }

        session.attempts++;

        if (!session.code.equals(code)) {
            if (session.attempts >= properties.getOtp().getMaxAttempts()) {
                activeOtps.remove(enrollmentId);
            }
            return new MfaVerificationResult(false, "Invalid verification code");
        }

        // Remove used OTP
        activeOtps.remove(enrollmentId);
        return new MfaVerificationResult(true, null);
    }

    private String generateOtpCode() {
        int length = properties.getOtp().getLength();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < length; i++) {
            code.append(secureRandom.nextInt(10));
        }

        return code.toString();
    }

    private void sendSmsOtp(String phoneNumber, String code) {
        String message = properties.getOtp().getSmsTemplate().replace("{code}", code);

        // In production, integrate with SMS provider:
        // - Twilio: send via Twilio API
        // - AWS SNS: use SNS publish
        // - Other providers as needed

        logger.info("SMS OTP would be sent to {}: {}", phoneNumber, message);

        // Mock implementation
        // smsService.send(phoneNumber, message);
    }

    private void sendEmailOtp(String emailAddress, String code) {
        String subject = "Your Verification Code";
        String message = properties.getOtp().getEmailTemplate().replace("{code}", code);

        // In production, integrate with email provider:
        // - SendGrid: use SendGrid API
        // - AWS SES: use SES sendEmail
        // - SMTP: use JavaMail
        // - Other providers as needed

        logger.info("Email OTP would be sent to {}: {}", emailAddress, message);

        // Mock implementation
        // emailService.send(emailAddress, subject, message);
    }

    private static class OtpSession {
        final String code;
        final Instant createdAt;
        final Instant expiresAt;
        int attempts = 0;

        OtpSession(String code, Instant createdAt, Instant expiresAt) {
            this.code = code;
            this.createdAt = createdAt;
            this.expiresAt = expiresAt;
        }
    }
}