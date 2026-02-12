package com.gogidix.rapidassist.orchestration.location.interfaces.rest;

import com.gogidix.rapidassist.orchestration.location.application.service.LocationService;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;
import com.gogidix.rapidassist.orchestration.location.interfaces.dto.request.UpdateLocationRequest;
import com.gogidix.rapidassist.orchestration.location.interfaces.dto.response.LocationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Location operations
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @Valid @RequestBody UpdateLocationRequest request
    ) {
        log.info("Updating location for entity: {} {}", entityType, entityId);

        Location location = mapToLocation(request);
        Location updated = locationService.updateLocation(
                request.getTenantId(),
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                entityId,
                location
        );

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<LocationResponse> getLocation(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam String tenantId
    ) {
        Location location = locationService.getLocation(
                tenantId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                entityId
        );
        return ResponseEntity.ok(mapToResponse(location));
    }

    @GetMapping("/{entityType}/bulk")
    public ResponseEntity<List<LocationResponse>> getLocations(
            @PathVariable String entityType,
            @RequestParam String tenantId,
            @RequestParam List<String> entityIds
    ) {
        List<Location> locations = locationService.getLocations(
                tenantId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                entityIds
        );
        return ResponseEntity.ok(locations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<LocationResponse>> findNearbyLocations(
            @RequestParam String tenantId,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam Double radius,
            @RequestParam(required = false) String entityType
    ) {
        List<Location> locations = locationService.findLocationsWithinRadius(
                tenantId, latitude, longitude, radius
        );

        if (entityType != null) {
            locations = locations.stream()
                    .filter(l -> l.getEntityType().name().equalsIgnoreCase(entityType))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(locations.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{entityType}/{entityId}/history")
    public ResponseEntity<List<LocationHistory>> getLocationHistory(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam String tenantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "100") Integer limit
    ) {
        if (startTime != null && endTime != null) {
            return ResponseEntity.ok(locationService.getLocationHistory(
                    tenantId,
                    com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                    entityId,
                    startTime,
                    endTime
            ));
        } else {
            return ResponseEntity.ok(locationService.getRecentHistory(
                    tenantId,
                    com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                    entityId,
                    limit
            ));
        }
    }

    @DeleteMapping("/{entityType}/{entityId}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @RequestParam String tenantId
    ) {
        locationService.deleteLocation(
                tenantId,
                com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType.valueOf(entityType.toUpperCase()),
                entityId
        );
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<LocationResponse>> bulkUpdateLocations(
            @Valid @RequestBody List<UpdateLocationRequest> requests
    ) {
        if (requests.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        String tenantId = requests.get(0).getTenantId();
        List<Location> locations = requests.stream()
                .map(this::mapToLocation)
                .collect(Collectors.toList());

        List<Location> updated = locationService.bulkUpdateLocations(tenantId, locations);

        return ResponseEntity.ok(updated.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));
    }

    private Location mapToLocation(UpdateLocationRequest request) {
        return Location.builder()
                .tenantId(request.getTenantId())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .altitude(request.getAltitude())
                .accuracy(request.getAccuracy())
                .bearing(request.getBearing())
                .speed(request.getSpeed())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .postalCode(request.getPostalCode())
                .status(request.getStatus() != null ? com.gogidix.rapidassist.orchestration.location.domain.model.Location.LocationStatus.valueOf(request.getStatus()) : com.gogidix.rapidassist.orchestration.location.domain.model.Location.LocationStatus.ACTIVE)
                .metadata(request.getMetadata())
                .provider(request.getProvider())
                .build();
    }

    private LocationResponse mapToResponse(Location location) {
        return LocationResponse.builder()
                .id(location.getId())
                .tenantId(location.getTenantId())
                .entityType(location.getEntityType().name())
                .entityId(location.getEntityId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .altitude(location.getAltitude())
                .accuracy(location.getAccuracy())
                .bearing(location.getBearing())
                .speed(location.getSpeed())
                .address(location.getAddress())
                .city(location.getCity())
                .state(location.getState())
                .country(location.getCountry())
                .postalCode(location.getPostalCode())
                .status(location.getStatus().name())
                .timestamp(location.getTimestamp())
                .lastUpdated(location.getLastUpdated())
                .active(location.getActive())
                .metadata(location.getMetadata())
                .provider(location.getProvider())
                .build();
    }
}
