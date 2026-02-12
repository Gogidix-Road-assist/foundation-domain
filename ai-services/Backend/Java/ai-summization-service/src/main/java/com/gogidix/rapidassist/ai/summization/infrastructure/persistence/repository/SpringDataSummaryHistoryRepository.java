package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity.SummaryHistoryEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SummaryHistoryEntity.
 */
@Repository
public interface SpringDataSummaryHistoryRepository extends MongoRepository<SummaryHistoryEntity, String> {

    Optional<SummaryHistoryEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    List<SummaryHistoryEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<SummaryHistoryEntity> findByUserIdAndTenantIdOrderByCreatedAtDesc(String userId, String tenantId);

    List<SummaryHistoryEntity> findBySummarizationTaskIdAndTenantId(UUID taskId, String tenantId);

    List<SummaryHistoryEntity> findByDocumentSummaryIdAndTenantId(UUID summaryId, String tenantId);

    List<SummaryHistoryEntity> findByTenantIdAndActionOrderByCreatedAtDesc(String tenantId, String action);

    @Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<SummaryHistoryEntity> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0 }")
    List<SummaryHistoryEntity> findRecentByTenantId(String tenantId);

    long countByTenantId(String tenantId);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);
}
