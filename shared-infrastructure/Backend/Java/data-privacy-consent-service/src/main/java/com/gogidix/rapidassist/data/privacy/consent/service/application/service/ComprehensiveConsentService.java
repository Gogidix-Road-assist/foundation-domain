package com.gogidix.rapidassist.data.privacy.consent.service.application.service;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.EnhancedConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb.EnhancedConsentPreferencesDocument;
import com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb.EnhancedConsentPreferencesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComprehensiveConsentService {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveConsentService.class);

    private final EnhancedConsentPreferencesRepository repository;

    public ComprehensiveConsentService(EnhancedConsentPreferencesRepository repository) {
        this.repository = repository;
    }

    public EnhancedConsentPreferences getConsent(String tenantId, String subject) {
        Optional<EnhancedConsentPreferencesDocument> document = repository.findByTenantIdAndSubject(tenantId, subject);
        return document.map(EnhancedConsentPreferencesDocument::toDomain)
                      .orElseGet(() -> createDefaultConsent(tenantId, subject));
    }

    public EnhancedConsentPreferences updateConsent(
            String tenantId,
            String subject,
            EnhancedConsentPreferences.ConsentType consentType,
            boolean granted,
            HttpServletRequest request) {

        logger.info("Updating consent: tenant={}, subject={}, type={}, granted={}",
            tenantId, subject, consentType, granted);

        EnhancedConsentPreferences current = getConsent(tenantId, subject);
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");

        // Update specific consent
        EnhancedConsentPreferences updated = current.updateConsent(consentType, granted, ipAddress, userAgent);

        // Save to database
        EnhancedConsentPreferencesDocument document = EnhancedConsentPreferencesDocument.fromDomain(updated);
        EnhancedConsentPreferencesDocument saved = repository.save(document);

        // Log for audit purposes
        logger.info("Consent updated successfully: tenant={}, subject={}, consentId={}",
            tenantId, subject, saved.id());

        return saved.toDomain();
    }

    public EnhancedConsentPreferences withdrawConsent(
            String tenantId,
            String subject,
            EnhancedConsentPreferences.ConsentType consentType,
            String reason,
            HttpServletRequest request) {

        logger.info("Withdrawing consent: tenant={}, subject={}, type={}, reason={}",
            tenantId, subject, consentType, reason);

        EnhancedConsentPreferences current = getConsent(tenantId, subject);
        String ipAddress = getClientIpAddress(request);

        // Withdraw consent
        EnhancedConsentPreferences withdrawn = current.withdrawConsent(consentType, reason);

        // Save to database
        EnhancedConsentPreferencesDocument document = EnhancedConsentPreferencesDocument.fromDomain(withdrawn);
        EnhancedConsentPreferencesDocument saved = repository.save(document);

        // Log for audit purposes
        logger.warn("Consent withdrawn: tenant={}, subject={}, type={}, reason={}",
            tenantId, subject, consentType, reason);

        return saved.toDomain();
    }

    public boolean hasValidConsent(String tenantId, String subject, EnhancedConsentPreferences.ConsentType consentType) {
        Optional<EnhancedConsentPreferencesDocument> document = repository.findByTenantIdAndSubject(tenantId, subject);
        if (document.isEmpty()) {
            return false;
        }

        EnhancedConsentPreferences preferences = document.get().toDomain();
        return preferences.hasConsent(consentType);
    }

    public boolean isAllRequiredConsentsGiven(String tenantId, String subject) {
        Optional<EnhancedConsentPreferencesDocument> document = repository.findByTenantIdAndSubject(tenantId, subject);
        if (document.isEmpty()) {
            return false;
        }

        EnhancedConsentPreferences preferences = document.get().toDomain();
        return preferences.isAllRequiredConsentsGiven();
    }

    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void processExpiredConsents() {
        logger.info("Processing expired consents");

        List<EnhancedConsentPreferencesDocument> expired = repository.findExpiredConsents(
            null, // All tenants
            Instant.now()
        );

        for (EnhancedConsentPreferencesDocument doc : expired) {
            EnhancedConsentPreferences expiredConsent = doc.toDomain();

            // Mark as expired
            EnhancedConsentPreferences updated = new EnhancedConsentPreferences(
                expiredConsent.tenantId(),
                expiredConsent.subject(),
                EnhancedConsentPreferences.ConsentStatus.EXPIRED,
                Instant.now(),
                expiredConsent.createdAt(),
                expiredConsent.consentGivenAt(),
                expiredConsent.consentExpiresAt(),
                expiredConsent.consents(),
                expiredConsent.ipAddress(),
                expiredConsent.userAgent(),
                expiredConsent.version(),
                expiredConsent.withdrawnConsents(),
                expiredConsent.metadata()
            );

            repository.save(EnhancedConsentPreferencesDocument.fromDomain(updated));
            logger.info("Marked consent as expired: tenant={}, subject={}",
                expiredConsent.tenantId(), expiredConsent.subject());
        }

        logger.info("Processed {} expired consents", expired.size());
    }

    public List<EnhancedConsentPreferences> getConsentHistory(String tenantId) {
        List<EnhancedConsentPreferencesDocument> documents = repository.findByTenantId(tenantId);
        return documents.stream()
                      .map(EnhancedConsentPreferencesDocument::toDomain)
                      .toList();
    }

    public void deleteAllConsents(String tenantId, String subject) {
        repository.deleteByTenantIdAndSubject(tenantId, subject);
        logger.warn("All consents deleted: tenant={}, subject={}", tenantId, subject);
    }

    private EnhancedConsentPreferences createDefaultConsent(String tenantId, String subject) {
        logger.debug("Creating default consent for tenant={}, subject={}", tenantId, subject);
        return EnhancedConsentPreferences.createDefault(tenantId, subject);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    // GDPR/CCPA compliance methods
    public byte[] exportUserData(String tenantId, String subject) {
        // Implementation for GDPR data export
        EnhancedConsentPreferences consent = getConsent(tenantId, subject);

        // In a real implementation, this would generate a JSON or CSV export
        // of all user data including consent history

        logger.info("Data export requested: tenant={}, subject={}", tenantId, subject);
        return consent.toString().getBytes(); // Simplified implementation
    }

    @Transactional
    public boolean anonymizeUserData(String tenantId, String subject) {
        logger.info("Anonymizing user data: tenant={}, subject={}", tenantId, subject);

        Optional<EnhancedConsentPreferencesDocument> document = repository.findByTenantIdAndSubject(tenantId, subject);
        if (document.isEmpty()) {
            return false;
        }

        // Create anonymized version
        EnhancedConsentPreferencesDocument doc = document.get();
        EnhancedConsentPreferencesDocument anonymized = new EnhancedConsentPreferencesDocument(
            doc.id(),
            "ANONYMIZED_" + System.currentTimeMillis(),
            "ANONYMIZED_SUBJECT",
            doc.status(),
            Instant.now(),
            doc.createdAt(),
            doc.consentGivenAt(),
            doc.consentExpiresAt(),
            doc.consents(),
            "0.0.0.0",
            "ANONYMIZED",
            doc.version(),
            doc.withdrawnConsents(),
            doc.metadata(),
            doc.versionNumber() + 1
        );

        repository.save(anonymized);
        return true;
    }
}