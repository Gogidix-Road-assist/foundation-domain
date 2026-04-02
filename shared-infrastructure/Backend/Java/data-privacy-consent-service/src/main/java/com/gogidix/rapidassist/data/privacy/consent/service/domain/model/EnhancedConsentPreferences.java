package com.gogidix.rapidassist.data.privacy.consent.service.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record EnhancedConsentPreferences(
    String tenantId,
    String subject,
    ConsentStatus status,
    Instant updatedAt,
    Instant createdAt,
    Instant consentGivenAt,
    Instant consentExpiresAt,
    Map<ConsentType, ConsentDetail> consents,
    String ipAddress,
    String userAgent,
    String version,
    List<String> withdrawnConsents,
    Map<String, Object> metadata
) {

    public static EnhancedConsentPreferences createDefault(String tenantId, String subject) {
        return new EnhancedConsentPreferences(
            tenantId,
            subject,
            ConsentStatus.PENDING,
            Instant.now(),
            Instant.now(),
            null,
            null,
            Map.of(
                ConsentType.TERMS_OF_SERVICE, new ConsentDetail(false, null, null),
                ConsentType.MARKETING_EMAILS, new ConsentDetail(false, null, null),
                ConsentType.ANALYTICS, new ConsentDetail(false, null, null),
                ConsentType.COOKIES, new ConsentDetail(false, null, null),
                ConsentType.DATA_PROCESSING, new ConsentDetail(false, null, null),
                ConsentType.THIRD_PARTY_SHARING, new ConsentDetail(false, null, null)
            ),
            null,
            null,
            "1.0",
            List.of(),
            Map.of()
        );
    }

    public boolean hasConsent(ConsentType type) {
        ConsentDetail detail = consents.get(type);
        return detail != null && detail.granted() && !withdrawnConsents.contains(type.name());
    }

    public boolean isAllRequiredConsentsGiven() {
        return requiredConsentTypes().stream().allMatch(this::hasConsent);
    }

    public EnhancedConsentPreferences updateConsent(ConsentType type, boolean granted, String ipAddress, String userAgent) {
        Map<ConsentType, ConsentDetail> updatedConsents = Map.copyOf(consents);
        ConsentDetail updatedDetail = new ConsentDetail(granted, Instant.now(), ipAddress);

        return new EnhancedConsentPreferences(
            tenantId,
            subject,
            granted ? ConsentStatus.GIVEN : status,
            Instant.now(),
            createdAt,
            granted ? Instant.now() : consentGivenAt,
            consentExpiresAt,
            updatedConsents,
            ipAddress,
            userAgent,
            version,
            withdrawnConsents,
            metadata
        );
    }

    public EnhancedConsentPreferences withdrawConsent(ConsentType type, String reason) {
        List<String> newWithdrawnConsents = List.copyOf(withdrawnConsents);
        if (!newWithdrawnConsents.contains(type.name())) {
            newWithdrawnConsents.add(type.name());
        }

        Map<String, Object> newMetadata = Map.copyOf(metadata);
        newMetadata.put("withdrawal_" + type.name() + "_reason", reason);
        newMetadata.put("withdrawal_" + type.name() + "_timestamp", Instant.now().toString());

        return new EnhancedConsentPreferences(
            tenantId,
            subject,
            ConsentStatus.PARTIALLY_WITHDRAWN,
            Instant.now(),
            createdAt,
            consentGivenAt,
            consentExpiresAt,
            consents,
            ipAddress,
            userAgent,
            version,
            newWithdrawnConsents,
            newMetadata
        );
    }

    private List<ConsentType> requiredConsentTypes() {
        return List.of(
            ConsentType.TERMS_OF_SERVICE,
            ConsentType.DATA_PROCESSING
        );
    }

    public enum ConsentStatus {
        PENDING,
        GIVEN,
        WITHDRAWN,
        PARTIALLY_WITHDRAWN,
        EXPIRED
    }

    public enum ConsentType {
        TERMS_OF_SERVICE,
        PRIVACY_POLICY,
        MARKETING_EMAILS,
        ANALYTICS,
        COOKIES,
        DATA_PROCESSING,
        THIRD_PARTY_SHARING,
        LOCATION_TRACKING,
        PERSONALIZATION,
        COMMUNICATION_PREFERENCES
    }

    public record ConsentDetail(
        boolean granted,
        Instant grantedAt,
        String ipAddress
    ) {}
}