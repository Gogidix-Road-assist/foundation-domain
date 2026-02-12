package com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoBillingAccountRepository extends MongoRepository<BillingAccountDocument, String> {

    Optional<BillingAccountDocument> findByTenantId(String tenantId);

    void deleteByTenantId(String tenantId);
}