package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationMemoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TranslationMemoryEntity.
 */
public interface SpringDataTranslationMemoryRepository extends MongoRepository<TranslationMemoryEntity, String> {

    /**
     * Find memory by UUID and tenant.
     */
    Optional<TranslationMemoryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find memory by source hash and tenant.
     */
    Optional<TranslationMemoryEntity> findBySourceHashAndTenantId(String sourceHash, String tenantId);

    /**
     * Find memories by language pair and tenant.
     */
    List<TranslationMemoryEntity> findBySourceLanguageAndTargetLanguageAndTenantId(
            String sourceLanguage, String targetLanguage, String tenantId);

    /**
     * Find verified memories by language pair and tenant.
     */
    List<TranslationMemoryEntity> findByVerifiedAndSourceLanguageAndTargetLanguageAndTenantId(
            Boolean verified, String sourceLanguage, String targetLanguage, String tenantId);

    /**
     * Find memories by domain and tenant.
     */
    List<TranslationMemoryEntity> findByDomainAndTenantId(String domain, String tenantId);

    /**
     * Find frequently used memories.
     */
    List<TranslationMemoryEntity> findByUsageCountGreaterThanAndTenantId(Integer minUsageCount, String tenantId);

    /**
     * Find stale memories (not used since threshold).
     */
    @Query("{ 'tenantId': ?0, 'lastUsedAt': { $lt: ?1 } }")
    List<TranslationMemoryEntity> findStaleMemories(String tenantId, LocalDateTime threshold);

    /**
     * Find all memories by tenant.
     */
    List<TranslationMemoryEntity> findByTenantId(String tenantId);

    /**
     * Count memories by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count verified memories by tenant.
     */
    long countByVerifiedAndTenantId(Boolean verified, String tenantId);

    /**
     * Delete memory by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if memory exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Check if source hash exists.
     */
    boolean existsBySourceHashAndTenantId(String sourceHash, String tenantId);
}
