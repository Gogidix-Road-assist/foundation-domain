package com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.payment.service.domain.model.EnhancedPaymentIntent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnhancedPaymentIntentRepository extends MongoRepository<EnhancedPaymentIntentDocument, String> {

    Optional<EnhancedPaymentIntentDocument> findByTenantIdAndIntentId(String tenantId, String intentId);

    List<EnhancedPaymentIntentDocument> findByTenantId(String tenantId);

    List<EnhancedPaymentIntentDocument> findByTenantIdAndCustomerId(String tenantId, String customerId);

    @Query("{ 'tenantId': ?0, 'status': ?1 }")
    List<EnhancedPaymentIntentDocument> findByTenantIdAndStatus(String tenantId, EnhancedPaymentIntent.PaymentStatus status);

    @Query("{ 'status': 'CREATED', 'expiresAt': { $lt: ?0 } }")
    List<EnhancedPaymentIntentDocument> findExpiredIntents(Instant now);

    @Query("{ 'status': 'REQUIRES_ACTION', 'updatedAt': { $gte: ?0 } }")
    List<EnhancedPaymentIntentDocument> findPendingActions(Instant since);

    @Query("{ 'fraudCheck.status': 'MANUAL_REVIEW' }")
    List<EnhancedPaymentIntentDocument> findPendingManualReview();

    void deleteByTenantIdAndIntentId(String tenantId, String intentId);
}