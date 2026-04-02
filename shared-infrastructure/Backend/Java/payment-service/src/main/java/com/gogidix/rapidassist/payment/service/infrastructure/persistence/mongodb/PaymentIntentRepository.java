package com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentIntentRepository extends MongoRepository<PaymentIntentDocument, String> {

    Optional<PaymentIntentDocument> findByTenantIdAndIntentId(String tenantId, String intentId);

    void deleteByTenantIdAndIntentId(String tenantId, String intentId);
}