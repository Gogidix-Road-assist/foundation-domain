package com.gogidix.rapidassist.ai.tagging.domain.repository;

import com.gogidix.rapidassist.ai.tagging.domain.model.TagSuggestion;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for TagSuggestion aggregate.
 * Defines the contract for TagSuggestion persistence operations.
 */
public interface TagSuggestionRepositoryPort {

    /**
     * Save a tag suggestion
     */
    TagSuggestion save(TagSuggestion suggestion);

    /**
     * Find suggestion by ID
     */
    Optional<TagSuggestion> findById(UUID id);

    /**
     * Find suggestion by tenant ID and suggestion ID
     */
    Optional<TagSuggestion> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all suggestions for a specific content
     */
    List<TagSuggestion> findByTenantIdAndContentId(String tenantId, String contentId);

    /**
     * Find pending suggestions for a specific content
     */
    List<TagSuggestion> findByTenantIdAndContentIdAndStatus(String tenantId, String contentId, TagSuggestion.SuggestionStatus status);

    /**
     * Find all suggestions by status
     */
    List<TagSuggestion> findByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status);

    /**
     * Find high confidence suggestions
     */
    List<TagSuggestion> findHighConfidenceSuggestions(String tenantId, double minConfidence);

    /**
     * Find expired suggestions
     */
    List<TagSuggestion> findExpiredSuggestions(String tenantId);

    /**
     * Delete suggestion by ID
     */
    void deleteById(UUID id);

    /**
     * Delete suggestion by tenant ID and suggestion ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Delete all suggestions for a specific content
     */
    void deleteByTenantIdAndContentId(String tenantId, String contentId);

    /**
     * Delete expired suggestions
     */
    void deleteExpiredSuggestions(String tenantId);

    /**
     * Count suggestions by tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Count suggestions by status
     */
    long countByTenantIdAndStatus(String tenantId, TagSuggestion.SuggestionStatus status);
}
