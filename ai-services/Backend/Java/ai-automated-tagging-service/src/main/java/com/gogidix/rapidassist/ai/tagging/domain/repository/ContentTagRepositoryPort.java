package com.gogidix.rapidassist.ai.tagging.domain.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.ContentTag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ContentTag aggregate.
 * Defines the contract for ContentTag persistence operations.
 */
public interface ContentTagRepositoryPort {

    /**
     * Save a content tag
     */
    ContentTag save(ContentTag contentTag);

    /**
     * Find content tag by ID
     */
    Optional<ContentTag> findById(UUID id);

    /**
     * Find content tag by tenant ID and ID
     */
    Optional<ContentTag> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all tags for a specific content
     */
    List<ContentTag> findByTenantIdAndContentId(String tenantId, String contentId);

    /**
     * Find all tags for a specific content by content type
     */
    List<ContentTag> findByTenantIdAndContentIdAndContentType(String tenantId, String contentId, String contentType);

    /**
     * Find all content tags for a specific tag
     */
    List<ContentTag> findByTenantIdAndTagId(String tenantId, UUID tagId);

    /**
     * Find content tags by tagging source
     */
    List<ContentTag> findByTenantIdAndTaggingSource(String tenantId, ContentTag.TaggingSource taggingSource);

    /**
     * Find unverified content tags
     */
    List<ContentTag> findByTenantIdAndManuallyVerifiedFalse(String tenantId);

    /**
     * Check if content is tagged with a specific tag
     */
    boolean existsByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId);

    /**
     * Delete content tag by ID
     */
    void deleteById(UUID id);

    /**
     * Delete content tag by tenant ID and ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Delete all tags for a specific content
     */
    void deleteByTenantIdAndContentId(String tenantId, String contentId);

    /**
     * Delete specific tag from content
     */
    void deleteByTenantIdAndContentIdAndTagId(String tenantId, String contentId, UUID tagId);

    /**
     * Count tags for content
     */
    long countByTenantIdAndContentId(String tenantId, String contentId);

    /**
     * Count usages of a specific tag
     */
    long countByTenantIdAndTagId(String tenantId, UUID tagId);
}
