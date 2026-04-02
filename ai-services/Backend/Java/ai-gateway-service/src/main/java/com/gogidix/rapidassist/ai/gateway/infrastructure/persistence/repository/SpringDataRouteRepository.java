package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RouteEntity;
import com.gogidix.rapidassist.ai.gateway.domain.model.RouteStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for RouteEntity.
 */
@Repository
public interface SpringDataRouteRepository extends MongoRepository<RouteEntity, String> {

    /**
     * Find route by UUID and tenant.
     */
    Optional<RouteEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find all routes by tenant.
     */
    List<RouteEntity> findByTenantId(String tenantId);

    /**
     * Find routes by tenant and status.
     */
    List<RouteEntity> findByTenantIdAndStatus(String tenantId, RouteStatus status);

    /**
     * Find routes by service ID and tenant.
     */
    List<RouteEntity> findByServiceIdAndTenantId(String serviceId, String tenantId);

    /**
     * Find route by path, HTTP method, and tenant.
     */
    Optional<RouteEntity> findByPathAndHttpMethodAndTenantId(String path, String httpMethod, String tenantId);

    /**
     * Find routes by tenant and status in list.
     */
    List<RouteEntity> findByTenantIdAndStatusIn(String tenantId, List<RouteStatus> statuses);

    /**
     * Check if route exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete route by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find routes ordered by priority (ascending).
     */
    List<RouteEntity> findByTenantIdOrderByPriorityAsc(String tenantId);

    /**
     * Find active routes ordered by priority.
     */
    List<RouteEntity> findByTenantIdAndStatusOrderByPriorityAsc(
            String tenantId, RouteStatus status);
}
