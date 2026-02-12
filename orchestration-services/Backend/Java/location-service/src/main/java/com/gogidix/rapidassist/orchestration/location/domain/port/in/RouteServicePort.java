package com.gogidix.rapidassist.orchestration.location.domain.port.in;

import com.gogidix.rapidassist.orchestration.location.domain.model.Route;

import java.util.List;
import java.util.Optional;

/**
 * Input port for Route operations
 */
public interface RouteServicePort {

    /**
     * Calculate route between two points
     */
    Route calculateRoute(
            String tenantId,
            String referenceId,
            Double startLat,
            Double startLon,
            Double endLat,
            Double endLon,
            String routeType
    );

    /**
     * Calculate route with waypoints
     */
    Route calculateRouteWithWaypoints(
            String tenantId,
            String referenceId,
            Route.RoutePoint startPoint,
            Route.RoutePoint endPoint,
            List<Route.RoutePoint> waypoints,
            String routeType
    );

    /**
     * Get route by ID
     */
    Route getRoute(String tenantId, String routeId);

    /**
     * Get route by reference ID
     */
    Optional<Route> getRouteByReferenceId(String referenceId);

    /**
     * Get routes by status
     */
    List<Route> getRoutesByStatus(String tenantId, Route.RouteStatus status);

    /**
     * Update route status
     */
    Route updateRouteStatus(String tenantId, String routeId, Route.RouteStatus status);

    /**
     * Clean up expired routes
     */
    void cleanupExpiredRoutes(String tenantId);

    /**
     * Recalculate route (e.g., for traffic updates)
     */
    Route recalculateRoute(String tenantId, String routeId);
}
