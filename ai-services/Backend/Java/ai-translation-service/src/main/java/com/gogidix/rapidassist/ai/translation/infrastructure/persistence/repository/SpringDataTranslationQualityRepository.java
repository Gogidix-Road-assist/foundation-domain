package com.gogidix.rapidassist.ai.translation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.translation.infrastructure.persistence.entity.TranslationQualityEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for TranslationQualityEntity.
 */
public interface SpringDataTranslationQualityRepository extends MongoRepository<TranslationQualityEntity, String> {

    /**
     * Find quality by UUID and tenant.
     */
    Optional<TranslationQualityEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find quality by translation request ID and tenant.
     */
    Optional<TranslationQualityEntity> findByTranslationRequestIdAndTenantId(UUID translationRequestId, String tenantId);

    /**
     * Find all qualities by translation request ID and tenant.
     */
    List<TranslationQualityEntity> findAllByTranslationRequestIdAndTenantId(UUID translationRequestId, String tenantId);

    /**
     * Find qualities by score range and tenant.
     */
    List<TranslationQualityEntity> findByScoreBetweenAndTenantId(Double minScore, Double maxScore, String tenantId);

    /**
     * Delete quality by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete by translation request ID and tenant.
     */
    void deleteByTranslationRequestIdAndTenantId(UUID translationRequestId, String tenantId);

    /**
     * Count qualities by tenant.
     */
    long countByTenantId(String tenantId);
}
