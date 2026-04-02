package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentPreferencesRepository extends MongoRepository<ConsentPreferencesDocument, String> {

    Optional<ConsentPreferencesDocument> findByTenantIdAndSubject(String tenantId, String subject);

    void deleteByTenantIdAndSubject(String tenantId, String subject);
}