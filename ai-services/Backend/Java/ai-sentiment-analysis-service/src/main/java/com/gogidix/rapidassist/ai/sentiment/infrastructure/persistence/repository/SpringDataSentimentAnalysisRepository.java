package com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.sentiment.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.sentiment.domain.model.SentimentType;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.entity.SentimentAnalysisEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for SentimentAnalysis
 */
@Repository
public interface SpringDataSentimentAnalysisRepository extends MongoRepository<SentimentAnalysisEntity, UUID> {

    /**
     * Find by tenant ID and ID
     */
    Optional<SentimentAnalysisEntity> findByTenantIdAndId(String tenantId, UUID id);

    /**
     * Find all by tenant ID
     */
    List<SentimentAnalysisEntity> findAllByTenantId(String tenantId);

    /**
     * Find by tenant ID and user ID
     */
    List<SentimentAnalysisEntity> findByTenantIdAndUserId(String tenantId, String userId);

    /**
     * Find by tenant ID and source
     */
    List<SentimentAnalysisEntity> findByTenantIdAndSourceTypeAndSourceId(String tenantId, String sourceType, String sourceId);

    /**
     * Find by tenant ID and status
     */
    List<SentimentAnalysisEntity> findByTenantIdAndStatus(String tenantId, AnalysisStatus status);

    /**
     * Find by tenant ID and sentiment type
     */
    List<SentimentAnalysisEntity> findByTenantIdAndOverallSentiment(String tenantId, SentimentType sentimentType);

    /**
     * Find by tenant ID and date range
     */
    List<SentimentAnalysisEntity> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete by tenant ID and ID
     */
    void deleteByTenantIdAndId(String tenantId, UUID id);

    /**
     * Count by tenant ID
     */
    long countByTenantId(String tenantId);

    /**
     * Check if exists by tenant ID and ID
     */
    boolean existsByTenantIdAndId(String tenantId, UUID id);
}
