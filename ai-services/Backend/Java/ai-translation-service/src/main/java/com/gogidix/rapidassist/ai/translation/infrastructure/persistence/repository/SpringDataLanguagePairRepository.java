package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.LanguagePairEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for LanguagePairEntity.
 */
public interface SpringDataLanguagePairRepository extends MongoRepository<LanguagePairEntity, String> {

    /**
     * Find language pair by UUID and tenant.
     */
    Optional<LanguagePairEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find language pair by language codes and tenant.
     */
    Optional<LanguagePairEntity> findBySourceLanguageAndTargetLanguageAndTenantId(
            String sourceLanguage, String targetLanguage, String tenantId);

    /**
     * Find supported language pairs by tenant.
     */
    List<LanguagePairEntity> findBySupportedAndTenantId(Boolean supported, String tenantId);

    /**
     * Find all language pairs by tenant.
     */
    List<LanguagePairEntity> findByTenantId(String tenantId);

    /**
     * Find language pairs by source language and tenant.
     */
    List<LanguagePairEntity> findBySourceLanguageAndTenantId(String sourceLanguage, String tenantId);

    /**
     * Find language pairs by target language and tenant.
     */
    List<LanguagePairEntity> findByTargetLanguageAndTenantId(String targetLanguage, String tenantId);

    /**
     * Find high-quality language pairs (quality >= 0.8).
     */
    List<LanguagePairEntity> findByQualityScoreGreaterThanEqualAndTenantId(Double minQualityScore, String tenantId);

    /**
     * Find specialized model language pairs.
     */
    List<LanguagePairEntity> findByRequiresSpecializedModelAndTenantId(Boolean requiresSpecializedModel, String tenantId);

    /**
     * Check if language pair exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if language pair exists by language codes.
     */
    boolean existsBySourceLanguageAndTargetLanguageAndTenantId(
            String sourceLanguage, String targetLanguage, String tenantId);

    /**
     * Count language pairs by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Delete language pair by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
