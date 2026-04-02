package com.gogidix.rapidassist.ai.tagging.domain.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TagCategory aggregate.
 * Defines the contract for TagCategory persistence operations.
 */
public interface TagCategoryRepositoryPort {

    /**
     * Save a tag category
     */
    TagCategory save(TagCategory category);

    /**
     * Find category by ID
     */
    Optional<TagCategory> findById(UUID id);

    /**
     * Find category by tenant ID and category ID
     */
    Optional<TagCategory> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all categories for a tenant
     */
    List<TagCategory> findByTenantId(String tenantId);

    /**
     * Find categories by tenant ID and status
     */
    List<TagCategory> findByTenantIdAndStatus(String tenantId, TagCategory.CategoryStatus status);

    /**
     * Find categories ordered by display order
     */
    List<TagCategory> findByTenantIdOrderByDisplayOrderAsc(String tenantId);

    /**
     * Check if category name exists for tenant
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * Delete category by ID
     */
    void deleteById(UUID id);

    /**
     * Delete category by tenant ID and category ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Count categories by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count categories by tenant and status
     */
    long countByTenantIdAndStatus(String tenantId, TagCategory.CategoryStatus status);
}
