package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RequestLogEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RequestLogEntity.
 */
@Repository
public interface SpringDataRequestLogRepository extends MongoRepository<RequestLogEntity, String> {

    /**
     * Find request log by UUID and tenant.
     */
    Optional<RequestLogEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all request logs by tenant.
     */
    List<RequestLogEntity> findByTenantId(String tenantId);

    /**
     * Find request logs by tenant and request ID.
     */
    List<RequestLogEntity> findByTenantIdAndRequestId(String tenantId, String requestId);

    /**
     * Find request logs by tenant and route ID.
     */
    List<RequestLogEntity> findByTenantIdAndRouteId(String tenantId, String routeId);

    /**
     * Find request logs by tenant and service ID.
     */
    List<RequestLogEntity> findByTenantIdAndServiceId(String tenantId, String serviceId);

    /**
     * Find request logs by tenant and date range.
     */
    List<RequestLogEntity> findByTenantIdAndCreatedAtBetween(
            String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find request logs by tenant and status code.
     */
    List<RequestLogEntity> findByTenantIdAndStatusCode(String tenantId, Integer statusCode);

    /**
     * Find request logs by tenant and success status.
     */
    List<RequestLogEntity> findByTenantIdAndSuccess(String tenantId, Boolean success);

    /**
     * Count request logs by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count request logs by tenant and success.
     */
    long countByTenantIdAndSuccess(String tenantId, Boolean success);

    /**
     * Delete request logs by tenant and created date before threshold.
     */
    @Query("{ 'tenantId': ?0, 'createdAt': { $lt: ?1 } }")
    void deleteByTenantIdAndCreatedAtBefore(String tenantId, LocalDateTime date);

    /**
     * Delete request log by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);
}
