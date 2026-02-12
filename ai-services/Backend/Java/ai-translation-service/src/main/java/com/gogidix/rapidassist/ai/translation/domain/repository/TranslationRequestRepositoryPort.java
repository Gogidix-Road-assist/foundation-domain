package com.gogidix.rapidassist.ai.translation.domain.repository;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for TranslationRequest.
 * Defines the contract for persisting and retrieving translation requests.
 */
public interface TranslationRequestRepositoryPort {

    /**
     * Save a translation request.
     */
    TranslationRequest save(String tenantId, TranslationRequest request);

    /**
     * Find a request by ID and tenant.
     */
    Optional<TranslationRequest> findById(String tenantId, UUID requestId);

    /**
     * Find requests by translation session ID and tenant.
     */
    List<TranslationRequest> findByTranslationSessionId(String tenantId, UUID translationSessionId);

    /**
     * Find requests by status and tenant.
     */
    List<TranslationRequest> findByStatus(String tenantId, String status);

    /**
     * Find pending requests for a tenant.
     */
    List<TranslationRequest> findPendingRequests(String tenantId);

    /**
     * Find in-progress requests for a tenant.
     */
    List<TranslationRequest> findInProgressRequests(String tenantId);

    /**
     * Find completed requests within date range.
     */
    List<TranslationRequest> findCompletedRequestsInDateRange(String tenantId,
                                                               java.time.LocalDateTime startDate,
                                                               java.time.LocalDateTime endDate);

    /**
     * Find requests by language pair.
     */
    List<TranslationRequest> findByLanguagePair(String tenantId, String sourceLanguage, String targetLanguage);

    /**
     * Delete requests by translation session ID.
     */
    void deleteByTranslationSessionId(String tenantId, UUID translationSessionId);

    /**
     * Count requests by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count requests by status and tenant.
     */
    long countByStatusAndTenantId(String status, String tenantId);
}
