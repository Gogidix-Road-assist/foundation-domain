package com.gogidix.rapidassist.ai.tagging.domain.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.Tag;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Tag aggregate.
 * Defines the contract for Tag persistence operations.
 */
public interface TagRepositoryPort {

    /**
     * Save a tag
     */
    Tag save(Tag tag);

    /**
     * Find tag by ID
     */
    Optional<Tag> findById(UUID id);

    /**
     * Find tag by tenant ID and tag ID
     */
    Optional<Tag> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all tags for a tenant
     */
    List<Tag> findByTenantId(String tenantId);

    /**
     * Find tags by tenant ID and status
     */
    List<Tag> findByTenantIdAndStatus(String tenantId, Tag.TagStatus status);

    /**
     * Find tags by category
     */
    List<Tag> findByTenantIdAndCategoryId(String tenantId, UUID categoryId);

    /**
     * Find tags by name (case-insensitive search)
     */
    List<Tag> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);

    /**
     * Find most used tags
     */
    List<Tag> findMostUsedTags(String tenantId, int limit);

    /**
     * Check if tag name exists for tenant
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * Delete tag by ID
     */
    void deleteById(UUID id);

    /**
     * Delete tag by tenant ID and tag ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Count tags by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count tags by tenant and status
     */
    long countByTenantIdAndStatus(String tenantId, Tag.TagStatus status);

    /**
     * Count content tags by tenant and tag ID
     */
    long countByTenantIdAndTagId(String tenantId, UUID tagId);
}
