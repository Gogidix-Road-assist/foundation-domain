package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.EnhancedConsentPreferences;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Document(collection = "enhanced_consent_preferences")
public record EnhancedConsentPreferencesDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("subject")
    String subject,

    @Field("status")
    EnhancedConsentPreferences.ConsentStatus status,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_at")
    Instant createdAt,

    @Field("consent_given_at")
    Instant consentGivenAt,

    @Field("consent_expires_at")
    Instant consentExpiresAt,

    @Field("consents")
    Map<EnhancedConsentPreferences.ConsentType, ConsentDetailEmbedded> consents,

    @Field("ip_address")
    String ipAddress,

    @Field("user_agent")
    String userAgent,

    @Field("version")
    String version,

    @Field("withdrawn_consents")
    List<String> withdrawnConsents,

    @Field("metadata")
    Map<String, Object> metadata,

    @Field("db_version")
    Long versionNumber
) {

    public static EnhancedConsentPreferencesDocument fromDomain(EnhancedConsentPreferences preferences) {
        Map<EnhancedConsentPreferences.ConsentType, ConsentDetailEmbedded> embeddedConsents =
            preferences.consents().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new ConsentDetailEmbedded(e.getValue().granted(), e.getValue().grantedAt(), e.getValue().ipAddress())
                ));

        return new EnhancedConsentPreferencesDocument(
            null, // MongoDB will generate ID
            preferences.tenantId(),
            preferences.subject(),
            preferences.status(),
            preferences.updatedAt(),
            preferences.createdAt(),
            preferences.consentGivenAt(),
            preferences.consentExpiresAt(),
            embeddedConsents,
            preferences.ipAddress(),
            preferences.userAgent(),
            preferences.version(),
            preferences.withdrawnConsents(),
            preferences.metadata(),
            1L
        );
    }

    public static EnhancedConsentPreferencesDocument updateFromDomain(EnhancedConsentPreferencesDocument existing,
                                                                   EnhancedConsentPreferences preferences) {
        Map<EnhancedConsentPreferences.ConsentType, ConsentDetailEmbedded> embeddedConsents =
            preferences.consents().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new ConsentDetailEmbedded(e.getValue().granted(), e.getValue().grantedAt(), e.getValue().ipAddress())
                ));

        return new EnhancedConsentPreferencesDocument(
            existing.id(),
            preferences.tenantId(),
            preferences.subject(),
            preferences.status(),
            preferences.updatedAt(),
            existing.createdAt(), // Preserve original creation time
            preferences.consentGivenAt(),
            preferences.consentExpiresAt(),
            embeddedConsents,
            preferences.ipAddress(),
            preferences.userAgent(),
            preferences.version(),
            preferences.withdrawnConsents(),
            preferences.metadata(),
            existing.versionNumber() + 1
        );
    }

    public EnhancedConsentPreferences toDomain() {
        Map<EnhancedConsentPreferences.ConsentType, EnhancedConsentPreferences.ConsentDetail> consents =
            this.consents().entrySet().stream()
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    e -> new EnhancedConsentPreferences.ConsentDetail(
                        e.getValue().granted(),
                        e.getValue().grantedAt(),
                        e.getValue().ipAddress()
                    )
                ));

        return new EnhancedConsentPreferences(
            tenantId(),
            subject(),
            status(),
            updatedAt(),
            createdAt(),
            consentGivenAt(),
            consentExpiresAt(),
            consents,
            ipAddress(),
            userAgent(),
            version(),
            withdrawnConsents(),
            metadata()
        );
    }

    public record ConsentDetailEmbedded(
        boolean granted,
        Instant grantedAt,
        String ipAddress
    ) {}
}