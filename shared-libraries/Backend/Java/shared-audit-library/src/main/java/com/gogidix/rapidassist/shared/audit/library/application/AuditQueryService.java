package com.gogidix.rapidassist.shared.audit.library.application;

import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogEntity;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Service for querying audit logs.
 * Provides methods to retrieve audit logs with various filters and pagination.
 *
 * <p>This service requires the database publisher to be enabled and configured.
 */
@Service
public class AuditQueryService {

    private final AuditLogRepository repository;

    public AuditQueryService(AuditLogRepository repository) {
        this.repository = repository;
    }

    /**
     * Find all audit logs for a specific tenant.
     *
     * @param tenantId the tenant ID
     * @return list of audit logs for the tenant
     */
    public List<AuditLogEntity> findByTenant(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    /**
     * Find all audit logs for a specific tenant with pagination.
     *
     * @param tenantId the tenant ID
     * @param pageable pagination parameters
     * @return page of audit logs
     */
    public Page<AuditLogEntity> findByTenant(String tenantId, Pageable pageable) {
        return repository.findAllByTenantId(tenantId, pageable);
    }

    /**
     * Find all audit logs by actor ID.
     *
     * @param actorId the actor ID
     * @return list of audit logs performed by the actor
     */
    public List<AuditLogEntity> findByActor(String actorId) {
        return repository.findByActorId(actorId);
    }

    /**
     * Find all audit logs for a specific entity.
     *
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of audit logs for the entity
     */
    public List<AuditLogEntity> findByEntity(String entityType, String entityId) {
        return repository.findByEntityTypeAndEntityId(entityType, entityId);
    }

    /**
     * Find audit logs within a date range.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of audit logs within the date range
     */
    public List<AuditLogEntity> findByDateRange(Instant startDate, Instant endDate) {
        return repository.findByOccurredAtBetween(startDate, endDate);
    }

    /**
     * Find audit logs by event type.
     *
     * @param eventType the event type
     * @return list of audit logs with the specified event type
     */
    public List<AuditLogEntity> findByEventType(String eventType) {
        return repository.findByEventType(eventType);
    }

    /**
     * Find audit logs by correlation ID.
     *
     * @param correlationId the correlation ID
     * @return list of audit logs with the correlation ID
     */
    public List<AuditLogEntity> findByCorrelationId(String correlationId) {
        return repository.findByCorrelationId(correlationId);
    }

    /**
     * Find audit logs by tenant and actor within a date range.
     *
     * @param tenantId the tenant ID
     * @param actorId  the actor ID
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of audit logs matching all criteria
     */
    public List<AuditLogEntity> findByTenantAndActorAndDateRange(
            String tenantId,
            String actorId,
            Instant startDate,
            Instant endDate) {
        return repository.findByTenantAndActorAndDateRange(tenantId, actorId, startDate, endDate);
    }

    /**
     * Find audit logs by tenant and entity.
     *
     * @param tenantId   the tenant ID
     * @param entityType the entity type
     * @param entityId   the entity ID
     * @return list of audit logs matching all criteria
     */
    public List<AuditLogEntity> findByTenantAndEntity(
            String tenantId,
            String entityType,
            String entityId) {
        return repository.findByTenantAndEntity(tenantId, entityType, entityId);
    }

    /**
     * Count audit logs by tenant.
     *
     * @param tenantId the tenant ID
     * @return count of audit logs for the tenant
     */
    public long countByTenant(String tenantId) {
        return repository.countByTenantId(tenantId);
    }

    /**
     * Find a specific audit log by event ID.
     *
     * @param eventId the event ID
     * @return the audit log entity, or null if not found
     */
    public AuditLogEntity findByEventId(String eventId) {
        return repository.findByEventId(eventId).orElse(null);
    }

    /**
     * Find all audit logs with pagination.
     *
     * @param pageable pagination parameters
     * @return page of audit logs
     */
    public Page<AuditLogEntity> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
