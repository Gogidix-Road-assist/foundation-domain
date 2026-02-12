package com.gogidix.rapidassist.ai.report.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportScheduleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for ReportScheduleEntity.
 */
@Repository
public interface SpringDataReportScheduleRepository extends MongoRepository<ReportScheduleEntity, String> {

    /**
     * Find schedule by UUID and tenant.
     */
    Optional<ReportScheduleEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all schedules by tenant.
     */
    List<ReportScheduleEntity> findByTenantId(String tenantId);

    /**
     * Find active schedules by tenant.
     */
    List<ReportScheduleEntity> findByTenantIdAndIsActiveTrue(String tenantId);

    /**
     * Find schedules by template ID and tenant.
     */
    List<ReportScheduleEntity> findByTemplateIdAndTenantId(UUID templateId, String tenantId);

    /**
     * Find schedules due for execution (nextRunTime <= currentTime and isActive = true).
     */
    @Query("{ 'isActive': true, 'nextRunTime': { $lte: ?0 } }")
    List<ReportScheduleEntity> findSchedulesDueForExecution(LocalDateTime currentTime);

    /**
     * Check if schedule exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete schedule by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count schedules by tenant.
     */
    long countByTenantId(String tenantId);
}
