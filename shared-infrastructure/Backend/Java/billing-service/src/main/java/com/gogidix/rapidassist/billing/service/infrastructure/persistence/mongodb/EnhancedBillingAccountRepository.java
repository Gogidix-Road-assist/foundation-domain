package com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnhancedBillingAccountRepository extends MongoRepository<EnhancedBillingAccountDocument, String> {

    Optional<EnhancedBillingAccountDocument> findByTenantId(String tenantId);

    void deleteByTenantId(String tenantId);
}