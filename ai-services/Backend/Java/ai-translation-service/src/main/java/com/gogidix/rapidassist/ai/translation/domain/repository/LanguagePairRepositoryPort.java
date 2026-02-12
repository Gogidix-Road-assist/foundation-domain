package com.gogidix.rapidassist.ai.translation.domain.repository;

import com.gogidix.rapidassist.ai.translation.domain.model.LanguagePair;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for LanguagePair.
 * Defines the contract for persisting and retrieving language pair configurations.
 */
public interface LanguagePairRepositoryPort {

    /**
     * Save a language pair.
     */
    LanguagePair save(String tenantId, LanguagePair languagePair);

    /**
     * Find a language pair by ID and tenant.
     */
    Optional<LanguagePair> findById(String tenantId, UUID languagePairId);

    /**
     * Find a language pair by language codes and tenant.
     */
    Optional<LanguagePair> findByLanguageCodes(String tenantId, String sourceLanguage, String targetLanguage);

    /**
     * Find all supported language pairs for a tenant.
     */
    List<LanguagePair> findSupported(String tenantId);

    /**
     * Find all language pairs for a tenant.
     */
    List<LanguagePair> findByTenantId(String tenantId);

    /**
     * Find language pairs by source language.
     */
    List<LanguagePair> findBySourceLanguage(String tenantId, String sourceLanguage);

    /**
     * Find language pairs by target language.
     */
    List<LanguagePair> findByTargetLanguage(String tenantId, String targetLanguage);

    /**
     * Find high-quality language pairs (quality score >= 0.8).
     */
    List<LanguagePair> findHighQualityPairs(String tenantId);

    /**
     * Find specialized model language pairs.
     */
    List<LanguagePair> findSpecializedModelPairs(String tenantId);

    /**
     * Delete a language pair by ID and tenant.
     */
    void delete(String tenantId, UUID languagePairId);

    /**
     * Check if language pair exists.
     */
    boolean exists(String tenantId, UUID languagePairId);

    /**
     * Check if language pair is supported.
     */
    boolean isSupported(String tenantId, String sourceLanguage, String targetLanguage);

    /**
     * Count language pairs by tenant.
     */
    long countByTenantId(String tenantId);
}
