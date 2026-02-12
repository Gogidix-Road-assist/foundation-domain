package com.gogidix.rapidassist.ai.sentiment.domain.repository;

import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for SentimentAnalysis aggregate
 */
public interface SentimentAnalysisRepositoryPort {

    /**
     * Save sentiment analysis
     */
    SentimentAnalysis save(SentimentAnalysis sentimentAnalysis);

    /**
     * Find by ID
     */
    Optional<SentimentAnalysis> findById(UUID id);

    /**
     * Find by tenant ID and ID
     */
    Optional<SentimentAnalysis> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all by tenant ID
     */
    List<SentimentAnalysis> findAllByTenantId(String tenantId);

    /**
     * Find by tenant ID and user ID
     */
    List<SentimentAnalysis> findByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Find by tenant ID and source
     */
    List<SentimentAnalysis> findByTenantIdAndSource(String tenantId, String sourceType, String sourceId);

    /**
     * Find by tenant ID and status
     */
    List<SentimentAnalysis> findByTenantIdAndStatus(String tenantId, String status);

    /**
     * Find by tenant ID and sentiment type
     */
    List<SentimentAnalysis> findByTenantIdAndSentimentType(String tenantId, String sentimentType);

    /**
     * Find by tenant ID and date range
     */
    List<SentimentAnalysis> findByTenantIdAndDateRange(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete by ID
     */
    void deleteById(UUID id);

    /**
     * Delete by tenant ID and ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Check if exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Count by tenant ID
     */
    long countByTenantId(String tenantId);
}
