package com.gogidix.rapidassist.ai.summization.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.summization.domain.model.Priority;
import com.gogidix.rapidassist.ai.summization.domain.model.TaskStatus;
import com.gogidix.rapidassist.ai.summization.infrastructure.persistence.entity.SummarizationTaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for SummarizationTaskEntity.
 */
@Repository
public interface SpringDataSummarizationTaskRepository extends MongoRepository<SummarizationTaskEntity, String> {

    Optional<SummarizationTaskEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    Optional<SummarizationTaskEntity> findByTaskIdAndTenantId(String taskId, String tenantId);

    List<SummarizationTaskEntity> findByTenantId(String tenantId);

    List<SummarizationTaskEntity> findByUserIdAndTenantId(String userId, String tenantId);

    List<SummarizationTaskEntity> findByStatusAndTenantId(TaskStatus status, String tenantId);

    List<SummarizationTaskEntity> findByTenantIdAndStatusIn(String tenantId, List<TaskStatus> statuses);

    List<SummarizationTaskEntity> findByTenantIdAndPriorityIn(String tenantId, List<Priority> priorities);

    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    long countByTenantId(String tenantId);

    @Query("{ 'tenantId': ?0, 'status': 'PENDING', 'priority': { $in: ?1 } }")
    List<SummarizationTaskEntity> findPendingTasksByPriority(String tenantId, List<Priority> priorities);

    @Query("{ 'tenantId': ?0, 'status': 'IN_PROGRESS' }")
    List<SummarizationTaskEntity> findInProgressTasksByTenant(String tenantId);

    @Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<SummarizationTaskEntity> findByTenantIdAndCreatedAtBetween(String tenantId, LocalDateTime start, LocalDateTime end);
}
