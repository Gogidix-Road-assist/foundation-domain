package com.gogidix.rapidassist.shared.audit.library.infrastructure.database;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for AuditLogEntity.
 * Provides query methods for retrieving audit logs with various filters.
 */
@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogEntity, String> {

    /**
     * Find all audit logs for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return list of audit logs for the tenant
     */
    List<AuditLogEntity> findByTenantId(String tenantId);

    /**
     * Find all audit logs for a specific tenant with pagination.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination parameters
     * @return page of audit logs for the tenant
     */
    Page<AuditLogEntity> findAllByTenantId(String tenantId, Pageable pageable);

    /**
     * Find all audit logs by actor ID.
     *
     * @param actorId the actor ID
     * @return list of audit logs performed by the actor
     */
    List<AuditLogEntity> findByActorId(String actorId);

    /**
     * Find all audit logs for a specific entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of audit logs for the entity
     */
    List<AuditLogEntity> findByEntityTypeAndEntityId(String entityType, String entityId);

    /**
     * Find audit logs within a date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of audit logs within the date range
     */
    List<AuditLogEntity> findByOccurredAtBetween(Instant startDate, Instant endDate);

    /**
     * Find audit logs by event type.
     *
     * @param eventType the event type
     * @return list of audit logs with the specified event type
     */
    List<AuditLogEntity> findByEventType(String eventType);

    /**
     * Find audit logs by correlation ID.
     *
     * @param correlationId the correlation ID
     * @return list of audit logs with the correlation ID
     */
    List<AuditLogEntity> findByCorrelationId(String correlationId);

    /**
     * Find audit log by event ID.
     *
     * @param eventId the event ID
     * @return optional containing the audit log
     */
    Optional<AuditLogEntity> findByEventId(String eventId);

    /**
     * Find audit logs by tenant and actor within a date range.
     *
     * @param tenantId the tenant ID
     * @param actorId  the actor ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of audit logs matching all criteria
     */
    @Query("{ 'tenantId': ?0, 'actorId': ?1, 'occurredAt': { $gte: ?2, $lte: ?3 } }")
    List<AuditLogEntity> findByTenantAndActorAndDateRange(
            String tenantId,
            String actorId,
            Instant startDate,
            Instant endDate
    );

    /**
     * Find audit logs by tenant and entity.
     *
     * @param tenantId   the tenant ID
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of audit logs matching all criteria
     */
    @Query("{ 'tenantId': ?0, 'entityType': ?1, 'entityId': ?2 }")
    List<AuditLogEntity> findByTenantAndEntity(
            String tenantId,
            String entityType,
            String entityId
    );

    /**
     * Count audit logs by tenant.
     *
     * @param tenantId the tenant ID
     * @return count of audit logs for the tenant
     */
    long countByTenantId(String tenantId);

    /**
     * Delete audit logs older than the specified date.
     * Used for retention policy cleanup.
     *
     * @param beforeDate the cutoff date
     * @return number of deleted records
     */
    long deleteByCreatedAtBefore(Instant beforeDate);
}
