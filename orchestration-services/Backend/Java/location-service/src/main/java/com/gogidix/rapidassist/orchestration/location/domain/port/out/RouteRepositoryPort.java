package com.gogidix.rapidassist.orchestration.location.domain.port.out;

import com.gogidix.rapidassist.orchestration.location.domain.model.Route;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for Route persistence operations
 */
public interface RouteRepositoryPort {

    Route save(Route route);

    Optional<Route> findById(String id);

    /**
     * Find route by reference ID (request, trip, etc.)
     */
    Optional<Route> findByReferenceId(String referenceId);

    /**
     * Find routes by status
     */
    List<Route> findByTenantIdAndStatus(
            String tenantId,
            Route.RouteStatus status
    );

    /**
     * Find expired routes
     */
    List<Route> findExpiredRoutes(String tenantId);

    /**
     * Find routes within time range
     */
    List<Route> findByTenantIdAndCreatedAtBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Delete old routes based on retention policy
     */
    void deleteRoutesOlderThan(String tenantId, LocalDateTime retentionDate);

    /**
     * Bulk delete expired routes
     */
    void deleteExpiredRoutes(String tenantId);

    /**
     * Delete route by ID
     */
    void deleteById(String id);
}
