package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.EnhancedConsentPreferences;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnhancedConsentPreferencesRepository extends MongoRepository<EnhancedConsentPreferencesDocument, String> {

    Optional<EnhancedConsentPreferencesDocument> findByTenantIdAndSubject(String tenantId, String subject);

    List<EnhancedConsentPreferencesDocument> findByTenantId(String tenantId);

    @Query("{ 'tenantId': ?0, 'status': 'GIVEN', 'consentExpiresAt': { $lt: ?1 } }")
    List<EnhancedConsentPreferencesDocument> findExpiredConsents(String tenantId, Instant now);

    @Query("{ 'withdrawnConsents': { $ne: [] }, 'updatedAt': { $gte: ?0 } }")
    List<EnhancedConsentPreferencesDocument> findRecentlyWithdrawn(Instant since);

    void deleteByTenantIdAndSubject(String tenantId, String subject);
}