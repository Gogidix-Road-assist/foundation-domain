package com.gogidix.rapidassist.mfa.service.infrastructure.provider.comprehensive;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.domain.port.out.MfaProvider;
import com.gogidix.rapidassist.mfa.service.infrastructure.provider.MfaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

/**
 * Comprehensive MFA Provider supporting TOTP, HOTP, SMS, Email, and Backup Codes.
 */
public class ComprehensiveMfaProvider implements MfaProvider {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveMfaProvider.class);

    // MFA Methods
    public static final String METHOD_TOTP = "TOTP";
    public static final String METHOD_HOTP = "HOTP";
    public static final String METHOD_SMS = "SMS";
    public static final String METHOD_EMAIL = "EMAIL";
    public static final String METHOD_BACKUP_CODE = "BACKUP_CODE";

    private final MongoTemplate mongoTemplate;
    private final MfaProperties properties;
    private final SecureRandom secureRandom;

    // Sub-providers for different methods
    private final TotpMfaProvider totpProvider;
    private final OtpDeliveryProvider otpDeliveryProvider;
    private final BackupCodeProvider backupCodeProvider;

    public ComprehensiveMfaProvider(MongoTemplate mongoTemplate, MfaProperties properties) {
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;
        this.secureRandom = new SecureRandom();
        this.totpProvider = new TotpMfaProvider();
        this.otpDeliveryProvider = new OtpDeliveryProvider(properties);
        this.backupCodeProvider = new BackupCodeProvider();

        logger.info("ComprehensiveMfaProvider initialized");
    }

    @Override
    public MfaEnrollment enroll(String tenantId, String subject, String method) {
        if (tenantId == null || subject == null || method == null) {
            throw new IllegalArgumentException("Tenant ID, subject, and method are required");
        }

        String enrollmentId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        try {
            MfaEnrollment enrollment;

            switch (method.toUpperCase()) {
                case METHOD_TOTP:
                    enrollment = enrollTotp(tenantId, subject, enrollmentId, now);
                    break;

                case METHOD_HOTP:
                    enrollment = enrollHotp(tenantId, subject, enrollmentId, now);
                    break;

                case METHOD_SMS:
                    enrollment = enrollSms(tenantId, subject, enrollmentId, now);
                    break;

                case METHOD_EMAIL:
                    enrollment = enrollEmail(tenantId, subject, enrollmentId, now);
                    break;

                case METHOD_BACKUP_CODE:
                    enrollment = enrollBackupCodes(tenantId, subject, enrollmentId, now);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported MFA method: " + method);
            }

            // Store enrollment
            MfaEnrollmentDocument document = new MfaEnrollmentDocument();
            document.setId(enrollmentId);
            document.setTenantId(tenantId);
            document.setSubject(subject);
            document.setMethod(method);
            document.setSecret(enrollment.secret());
            document.setCreatedAt(now);
            document.setActive(true);
            document.setMetadata(enrollment.metadata());

            mongoTemplate.save(document);

            logger.info("MFA enrollment created for tenant: {}, subject: {}, method: {}",
                       tenantId, subject, method);

            return enrollment;

        } catch (Exception e) {
            logger.error("Failed to enroll MFA for tenant: {}, subject: {}, method: {}",
                        tenantId, subject, method, e);
            throw new RuntimeException("MFA enrollment failed", e);
        }
    }

    @Override
    public MfaVerificationResult verify(String tenantId, String subject, String enrollmentId, String code) {
        if (tenantId == null || subject == null || enrollmentId == null || code == null) {
            return new MfaVerificationResult(false, "Missing required verification parameters");
        }

        try {
            // Find enrollment
            Query query = new Query(Criteria.where("tenantId").is(tenantId)
                                  .and("subject").is(subject)
                                  .and("_id").is(enrollmentId)
                                  .and("active").is(true));

            MfaEnrollmentDocument enrollment = mongoTemplate.findOne(query, MfaEnrollmentDocument.class);

            if (enrollment == null) {
                return new MfaVerificationResult(false, "MFA enrollment not found or inactive");
            }

            // Verify based on method
            MfaVerificationResult result;
            switch (enrollment.getMethod().toUpperCase()) {
                case METHOD_TOTP:
                case METHOD_HOTP:
                    result = totpProvider.verify(enrollment.getSecret(), code);
                    break;

                case METHOD_SMS:
                case METHOD_EMAIL:
                    result = otpDeliveryProvider.verify(enrollment.getSecret(), code);
                    break;

                case METHOD_BACKUP_CODE:
                    result = backupCodeProvider.verify(enrollment.getSecret(), code);
                    if (result.verified()) {
                        // Mark backup code as used
                        backupCodeProvider.markCodeUsed(enrollment.getSecret(), code);
                    }
                    break;

                default:
                    return new MfaVerificationResult(false, "Unsupported MFA method");
            }

            // Update last used timestamp if successful
            if (result.verified()) {
                updateLastUsed(enrollmentId);
            }

            return result;

        } catch (Exception e) {
            logger.error("MFA verification failed for tenant: {}, subject: {}", tenantId, subject, e);
            return new MfaVerificationResult(false, "Verification failed due to system error");
        }
    }

    /**
     * Send OTP code for SMS/Email methods
     */
    public MfaEnrollment sendOtp(String tenantId, String subject, String enrollmentId) {
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                              .and("subject").is(subject)
                              .and("_id").is(enrollmentId)
                              .and("active").is(true));

        MfaEnrollmentDocument enrollment = mongoTemplate.findOne(query, MfaEnrollmentDocument.class);

        if (enrollment == null) {
            throw new IllegalArgumentException("MFA enrollment not found");
        }

        String method = enrollment.getMethod().toUpperCase();
        if (!METHOD_SMS.equals(method) && !METHOD_EMAIL.equals(method)) {
            throw new IllegalArgumentException("OTP sending only supported for SMS and Email methods");
        }

        // Generate and send OTP
        String code = otpDeliveryProvider.generateAndSend(enrollmentId, enrollment.getSubject(), method);

        // Store temporary OTP for verification
        updateTemporaryOtp(enrollmentId, code);

        logger.info("OTP sent for tenant: {}, subject: {}, method: {}", tenantId, subject, method);

        // Return enrollment without secret for security
        return new MfaEnrollment(enrollment.getTenantId(), enrollment.getSubject(),
                               enrollment.getMethod(), enrollment.getId(), enrollment.getCreatedAt());
    }

    private MfaEnrollment enrollTotp(String tenantId, String subject, String enrollmentId, Instant now) {
        String secret = totpProvider.generateSecret();
        String qrUrl = totpProvider.generateQrUrl(subject, secret);

        return new MfaEnrollment(tenantId, subject, METHOD_TOTP, enrollmentId, now)
                .withSecret(secret)
                .withMetadata("qrUrl", qrUrl);
    }

    private MfaEnrollment enrollHotp(String tenantId, String subject, String enrollmentId, Instant now) {
        String secret = totpProvider.generateSecret();

        return new MfaEnrollment(tenantId, subject, METHOD_HOTP, enrollmentId, now)
                .withSecret(secret);
    }

    private MfaEnrollment enrollSms(String tenantId, String subject, String enrollmentId, Instant now) {
        return new MfaEnrollment(tenantId, subject, METHOD_SMS, enrollmentId, now)
                .withSecret("SMS_OTP");
    }

    private MfaEnrollment enrollEmail(String tenantId, String subject, String enrollmentId, Instant now) {
        return new MfaEnrollment(tenantId, subject, METHOD_EMAIL, enrollmentId, now)
                .withSecret("EMAIL_OTP");
    }

    private MfaEnrollment enrollBackupCodes(String tenantId, String subject, String enrollmentId, Instant now) {
        String backupCodesJson = backupCodeProvider.generateBackupCodes();

        return new MfaEnrollment(tenantId, subject, METHOD_BACKUP_CODE, enrollmentId, now)
                .withSecret(backupCodesJson)
                .withMetadata("type", "backup_codes");
    }

    private void updateLastUsed(String enrollmentId) {
        Query query = new Query(Criteria.where("_id").is(enrollmentId));
        Update update = new Update().set("lastUsedAt", Instant.now());
        mongoTemplate.updateFirst(query, update, MfaEnrollmentDocument.class);
    }

    private void updateTemporaryOtp(String enrollmentId, String otp) {
        Query query = new Query(Criteria.where("_id").is(enrollmentId));
        Update update = new Update()
                .set("temporaryOtp", otp)
                .set("otpGeneratedAt", Instant.now())
                .set("otpExpiresAt", Instant.now().plusSeconds(properties.getOtpExpirationSeconds()));
        mongoTemplate.updateFirst(query, update, MfaEnrollmentDocument.class);
    }

    // MongoDB document model
    private static class MfaEnrollmentDocument {
        private String id;
        private String tenantId;
        private String subject;
        private String method;
        private String secret;
        private Instant createdAt;
        private Instant lastUsedAt;
        private boolean active;
        private String temporaryOtp;
        private Instant otpGeneratedAt;
        private Instant otpExpiresAt;
        private java.util.Map<String, Object> metadata;

        // Getters and setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTenantId() { return tenantId; }
        public void setTenantId(String tenantId) { this.tenantId = tenantId; }
        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }
        public String getMethod() { return method; }
        public void setMethod(String method) { this.method = method; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public Instant getCreatedAt() { return createdAt; }
        public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
        public Instant getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(Instant lastUsedAt) { this.lastUsedAt = lastUsedAt; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public String getTemporaryOtp() { return temporaryOtp; }
        public void setTemporaryOtp(String temporaryOtp) { this.temporaryOtp = temporaryOtp; }
        public Instant getOtpGeneratedAt() { return otpGeneratedAt; }
        public void setOtpGeneratedAt(Instant otpGeneratedAt) { this.otpGeneratedAt = otpGeneratedAt; }
        public Instant getOtpExpiresAt() { return otpExpiresAt; }
        public void setOtpExpiresAt(Instant otpExpiresAt) { this.otpExpiresAt = otpExpiresAt; }
        public java.util.Map<String, Object> getMetadata() { return metadata; }
        public void setMetadata(java.util.Map<String, Object> metadata) { this.metadata = metadata; }
    }
}