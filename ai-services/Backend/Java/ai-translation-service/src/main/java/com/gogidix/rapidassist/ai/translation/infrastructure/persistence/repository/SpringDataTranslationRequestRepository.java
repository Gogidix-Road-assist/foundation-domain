package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationRequestEntity;
import com.gogidix.rapidassist.ai.translation.domain.model.TranslationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TranslationRequestEntity.
 */
public interface SpringDataTranslationRequestRepository extends MongoRepository<TranslationRequestEntity, String> {

    /**
     * Find request by UUID and tenant.
     */
    Optional<TranslationRequestEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find requests by translation session ID and tenant.
     */
    List<TranslationRequestEntity> findByTranslationSessionIdAndTenantId(UUID translationSessionId, String tenantId);

    /**
     * Find requests by status and tenant.
     */
    List<TranslationRequestEntity> findByStatusAndTenantId(TranslationStatus status, String tenantId);

    /**
     * Find requests by language pair and tenant.
     */
    List<TranslationRequestEntity> findBySourceLanguageAndTargetLanguageAndTenantId(
            String sourceLanguage, String targetLanguage, String tenantId);

    /**
     * Find completed requests within date range.
     */
    @Query("{ 'tenantId': ?0, 'status': 'COMPLETED', 'completedAt': { $gte: ?1, $lte: ?2 } }")
    List<TranslationRequestEntity> findCompletedRequestsInDateRange(
            String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete requests by translation session ID and tenant.
     */
    void deleteByTranslationSessionIdAndTenantId(UUID translationSessionId, String tenantId);

    /**
     * Count requests by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count requests by status and tenant.
     */
    long countByStatusAndTenantId(TranslationStatus status, String tenantId);

    /**
     * Find requests by cache flag and tenant.
     */
    List<TranslationRequestEntity> findByFromCacheAndTenantId(Boolean fromCache, String tenantId);
}
