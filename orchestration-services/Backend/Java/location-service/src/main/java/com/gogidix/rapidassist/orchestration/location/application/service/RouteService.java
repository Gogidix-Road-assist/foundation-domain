package com.gogidix.rapidassist.orchestration.location.application.service;

import com.gogidix.rapidassist.orchestration.location.domain.model.Route;
import com.gogidix.rapidassist.orchestration.location.domain.port.in.RouteServicePort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.RouteRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka.RouteEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application service for Route operations
 * Integrates with external routing providers (Google Maps, OSRM, Mapbox)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService implements RouteServicePort {

    private final RouteRepositoryPort routeRepository;
    private final RouteEventPublisher eventPublisher;
    // TODO: Inject external routing service clients (Google, OSRM, etc.)

    @Override
    @Transactional
    public Route calculateRoute(
            String tenantId,
            String referenceId,
            Double startLat,
            Double startLon,
            Double endLat,
            Double endLon,
            String routeType
    ) {
        log.info("Calculating route for tenant: {} from ({}, {}) to ({}, {})", tenantId, startLat, startLon, endLat, endLon);

        Route.RoutePoint startPoint = Route.RoutePoint.builder()
                .latitude(startLat)
                .longitude(startLon)
                .build();

        Route.RoutePoint endPoint = Route.RoutePoint.builder()
                .latitude(endLat)
                .longitude(endLon)
                .build();

        return calculateRouteWithWaypoints(tenantId, referenceId, startPoint, endPoint, List.of(), routeType);
    }

    @Override
    @Transactional
    public Route calculateRouteWithWaypoints(
            String tenantId,
            String referenceId,
            Route.RoutePoint startPoint,
            Route.RoutePoint endPoint,
            List<Route.RoutePoint> waypoints,
            String routeType
    ) {
        log.info("Calculating route with waypoints for tenant: {}, reference: {}", tenantId, referenceId);

        // Check if route already exists and is valid
        Optional<Route> existingRoute = routeRepository.findByReferenceId(referenceId);
        if (existingRoute.isPresent() && existingRoute.get().isValid()) {
            log.debug("Using existing valid route: {}", existingRoute.get().getId());
            return existingRoute.get();
        }

        // Create new route
        Route route = Route.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .referenceId(referenceId)
                .startPoint(startPoint)
                .endPoint(endPoint)
                .waypoints(waypoints)
                .routeType(routeType != null ? routeType : "FASTEST")
                .status(Route.RouteStatus.CALCULATING)
                .createdAt(LocalDateTime.now())
                .provider("osrm") // Default provider
                .build();

        route = routeRepository.save(route);

        // Calculate route using external service
        try {
            Route calculatedRoute = calculateRouteFromExternalService(route);
            route = routeRepository.save(calculatedRoute);
            eventPublisher.publishRouteCalculated(route);
        } catch (Exception e) {
            log.error("Failed to calculate route: {}", e.getMessage());
            route.setStatus(Route.RouteStatus.FAILED);
            routeRepository.save(route);
            throw new RuntimeException("Failed to calculate route", e);
        }

        return route;
    }

    @Override
    public Route getRoute(String tenantId, String routeId) {
        return routeRepository.findById(routeId)
                .filter(r -> r.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));
    }

    @Override
    public Optional<Route> getRouteByReferenceId(String referenceId) {
        return routeRepository.findByReferenceId(referenceId);
    }

    @Override
    public List<Route> getRoutesByStatus(String tenantId, Route.RouteStatus status) {
        return routeRepository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    @Transactional
    public Route updateRouteStatus(String tenantId, String routeId, Route.RouteStatus status) {
        Route route = getRoute(tenantId, routeId);
        route.setStatus(status);
        return routeRepository.save(route);
    }

    @Override
    @Transactional
    public void cleanupExpiredRoutes(String tenantId) {
        log.info("Cleaning up expired routes for tenant: {}", tenantId);
        routeRepository.deleteExpiredRoutes(tenantId);
    }

    @Override
    @Transactional
    public Route recalculateRoute(String tenantId, String routeId) {
        log.info("Recalculating route: {} for tenant: {}", routeId, tenantId);

        Route existingRoute = getRoute(tenantId, routeId);

        if (existingRoute.getStartPoint() == null || existingRoute.getEndPoint() == null) {
            throw new IllegalStateException("Cannot recalculate route without start and end points");
        }

        Route newRoute = calculateRouteWithWaypoints(
                tenantId,
                existingRoute.getReferenceId(),
                existingRoute.getStartPoint(),
                existingRoute.getEndPoint(),
                existingRoute.getWaypoints() != null ? existingRoute.getWaypoints() : List.of(),
                existingRoute.getRouteType()
        );

        // Delete old route
        routeRepository.deleteById(routeId);

        return newRoute;
    }

    /**
     * Call external routing service to calculate route
     * This is a placeholder - actual implementation would call OSRM, Google Maps, etc.
     */
    private Route calculateRouteFromExternalService(Route route) {
        log.debug("Calling external routing service for route: {}", route.getId());

        // Placeholder implementation
        // In production, this would call OSRM, Google Maps API, Mapbox, etc.
        // For now, return a basic route structure

        double distance = calculateDistance(
                route.getStartPoint().getLatitude(),
                route.getStartPoint().getLongitude(),
                route.getEndPoint().getLatitude(),
                route.getEndPoint().getLongitude()
        );

        long duration = (long) (distance / 15.0 * 3600); // Assume 15 m/s average speed

        route.setStatus(Route.RouteStatus.COMPLETED);
        route.setTotalDistance(distance);
        route.setTotalDuration(duration);
        route.setEstimatedTimeOfArrival(System.currentTimeMillis() / 1000 + duration);
        route.setTrafficCondition("LIGHT");
        route.setTrafficDelay(0.0);
        route.setExpiresAt(LocalDateTime.now().plusHours(1));

        // Create basic step
        Route.RouteStep step = Route.RouteStep.builder()
                .stepNumber(1)
                .instruction("Proceed to destination")
                .distance(distance)
                .duration(duration)
                .startPoint(route.getStartPoint())
                .endPoint(route.getEndPoint())
                .maneuver("straight")
                .build();

        route.setSteps(List.of(step));

        return route;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Earth radius in meters

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
