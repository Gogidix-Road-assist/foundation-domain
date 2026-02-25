package com.gogidix.rapidassist.ai.contentanalysis.application.port.out;

import com.gogidix.rapidassist.ai.contentanalysis.domain.model.ContentTopic;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Content Topic entity.
 * Defines the contract for persisting and retrieving content topic data.
 */
public interface ContentTopicRepositoryPort {

    /**
     * Save content topic (create or update)
     */
    ContentTopic save(ContentTopic topic);

    /**
     * Save all content topics
     */
    List<ContentTopic> saveAll(List<ContentTopic> topics);

    /**
     * Find content topic by ID
     */
    Optional<ContentTopic> findById(UUID id);

    /**
     * Find content topic by ID and tenant ID
     */
    Optional<ContentTopic> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all topics for a specific content
     */
    List<ContentTopic> findByContentId(String contentId);

    /**
     * Find all topics for a specific content and tenant
     */
    List<ContentTopic> findByContentIdAndTenantId(String contentId, String tenantId);

    /**
     * Find all topics for a tenant
     */
    List<ContentTopic> findByTenantId(String tenantId);

    /**
     * Find topics by relevance score threshold
     */
    List<ContentTopic> findByRelevanceScoreGreaterThanEqual(Double threshold);

    /**
     * Find topics by category
     */
    List<ContentTopic> findByTopicCategory(String category);

    /**
     * Delete topics by content ID
     */
    void deleteByContentId(String contentId);

    /**
     * Delete topics by content ID and tenant ID
     */
    void deleteByContentIdAndTenantId(String contentId, String tenantId);

    /**
     * Delete topic by ID
     */
    void deleteById(UUID id);

    /**
     * Delete topic by ID and tenant ID
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);
}
