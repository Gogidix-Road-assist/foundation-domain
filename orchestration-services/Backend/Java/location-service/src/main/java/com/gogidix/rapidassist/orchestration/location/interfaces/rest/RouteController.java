package com.gogidix.rapidassist.orchestration.location.interfaces.rest;

import com.gogidix.rapidassist.orchestration.location.application.service.RouteService;
import com.gogidix.rapidassist.orchestration.location.domain.model.Route;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Route operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/calculate")
    public ResponseEntity<Route> calculateRoute(
            @RequestParam String tenantId,
            @RequestParam String referenceId,
            @RequestParam Double startLat,
            @RequestParam Double startLon,
            @RequestParam Double endLat,
            @RequestParam Double endLon,
            @RequestParam(defaultValue = "FASTEST") String routeType
    ) {
        log.info("Calculating route for tenant: {}", tenantId);
        Route route = routeService.calculateRoute(
                tenantId, referenceId, startLat, startLon, endLat, endLon, routeType
        );
        return ResponseEntity.ok(route);
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<Route> getRoute(
            @PathVariable String routeId,
            @RequestParam String tenantId
    ) {
        Route route = routeService.getRoute(tenantId, routeId);
        return ResponseEntity.ok(route);
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<Route> getRouteByReferenceId(@PathVariable String referenceId) {
        return routeService.getRouteByReferenceId(referenceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Route>> getRoutesByStatus(
            @PathVariable String status,
            @RequestParam String tenantId
    ) {
        List<Route> routes = routeService.getRoutesByStatus(
                tenantId,
                Route.RouteStatus.valueOf(status.toUpperCase())
        );
        return ResponseEntity.ok(routes);
    }

    @PatchMapping("/{routeId}/status")
    public ResponseEntity<Route> updateRouteStatus(
            @PathVariable String routeId,
            @RequestParam String tenantId,
            @RequestParam String status
    ) {
        Route route = routeService.updateRouteStatus(
                tenantId,
                routeId,
                Route.RouteStatus.valueOf(status.toUpperCase())
        );
        return ResponseEntity.ok(route);
    }

    @PostMapping("/{routeId}/recalculate")
    public ResponseEntity<Route> recalculateRoute(
            @PathVariable String routeId,
            @RequestParam String tenantId
    ) {
        log.info("Recalculating route: {} for tenant: {}", routeId, tenantId);
        Route route = routeService.recalculateRoute(tenantId, routeId);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/expired")
    public ResponseEntity<Void> cleanupExpiredRoutes(@RequestParam String tenantId) {
        routeService.cleanupExpiredRoutes(tenantId);
        return ResponseEntity.noContent().build();
    }
}
