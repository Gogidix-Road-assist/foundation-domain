package com.gogidix.rapidassist.ai.translation.domain.repository;

import com.gogidix.rapidassist.ai.translation.domain.model.TranslationMemory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for TranslationMemory.
 * Defines the contract for persisting and retrieving translation memory entries.
 */
public interface TranslationMemoryRepositoryPort {

    /**
     * Save a translation memory entry.
     */
    TranslationMemory save(String tenantId, TranslationMemory memory);

    /**
     * Find a memory entry by ID and tenant.
     */
    Optional<TranslationMemory> findById(String tenantId, UUID memoryId);

    /**
     * Find memory entries by source hash and tenant.
     */
    Optional<TranslationMemory> findBySourceHash(String tenantId, String sourceHash);

    /**
     * Find memory entries by source segment (exact match).
     */
    List<TranslationMemory> findBySourceSegment(String tenantId, String sourceSegment);

    /**
     * Find memory entries by language pair.
     */
    List<TranslationMemory> findByLanguagePair(String tenantId, String sourceLanguage, String targetLanguage);

    /**
     * Find similar source segments (fuzzy match).
     */
    List<TranslationMemory> findSimilarSegments(String tenantId, String sourceSegment,
                                                  String sourceLanguage, String targetLanguage, double minSimilarity);

    /**
     * Find verified memory entries by language pair.
     */
    List<TranslationMemory> findVerifiedByLanguagePair(String tenantId, String sourceLanguage, String targetLanguage);

    /**
     * Find memory entries by domain/context.
     */
    List<TranslationMemory> findByDomain(String tenantId, String domain);

    /**
     * Find frequently used entries.
     */
    List<TranslationMemory> findFrequentlyUsed(String tenantId, int minUsageCount);

    /**
     * Find stale entries (not used in specified days).
     */
    List<TranslationMemory> findStaleEntries(String tenantId, int staleThresholdDays);

    /**
     * Find all memory entries for a tenant.
     */
    List<TranslationMemory> findByTenantId(String tenantId);

    /**
     * Delete a memory entry by ID and tenant.
     */
    void delete(String tenantId, UUID memoryId);

    /**
     * Check if memory entry exists.
     */
    boolean exists(String tenantId, UUID memoryId);

    /**
     * Count memory entries by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count verified entries by tenant.
     */
    long countVerifiedByTenantId(String tenantId);
}
