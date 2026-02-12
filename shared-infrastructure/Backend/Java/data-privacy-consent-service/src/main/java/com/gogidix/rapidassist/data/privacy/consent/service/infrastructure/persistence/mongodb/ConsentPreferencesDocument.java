package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "consent_preferences")
public record ConsentPreferencesDocument(

    @Id
    String id,

    @Field("tenant_id")
    String tenantId,

    @Field("subject")
    String subject,

    @Field("terms_accepted")
    boolean termsAccepted,

    @Field("marketing_emails")
    boolean marketingEmails,

    @Field("updated_at")
    Instant updatedAt,

    @Field("created_at")
    Instant createdAt,

    @Field("version")
    Long version
) {

    public static ConsentPreferencesDocument fromDomain(ConsentPreferences preferences) {
        Instant now = Instant.now();
        return new ConsentPreferencesDocument(
            null, // MongoDB will generate ID
            preferences.tenantId(),
            preferences.subject(),
            preferences.termsAccepted(),
            preferences.marketingEmails(),
            preferences.updatedAt() != null ? preferences.updatedAt() : now,
            now,
            1L
        );
    }

    public static ConsentPreferencesDocument updateFromDomain(ConsentPreferencesDocument existing,
                                                             ConsentPreferences preferences) {
        return new ConsentPreferencesDocument(
            existing.id(),
            preferences.tenantId(),
            preferences.subject(),
            preferences.termsAccepted(),
            preferences.marketingEmails(),
            preferences.updatedAt() != null ? preferences.updatedAt() : Instant.now(),
            existing.createdAt(),
            existing.version() + 1
        );
    }

    public ConsentPreferences toDomain() {
        return new ConsentPreferences(
            tenantId(),
            subject(),
            termsAccepted(),
            marketingEmails(),
            updatedAt()
        );
    }
}