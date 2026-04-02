package com.gogidix.rapidassist.geo.location.service.domain.port.in;

import com.gogidix.rapidassist.geo.location.service.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface GeoLocationCommand {

    Location storeLocation(String tenantId, Coordinates coordinates, Location.LocationAccuracy accuracy,
                          String deviceId, String userId, Location.LocationSource source);

    Location storeLocationFromIp(String tenantId, String ipAddress);

    Location geocodeAddress(String tenantId, String address);

    List<Location> reverseGeocode(String tenantId, Coordinates coordinates);

    List<Location> getLocationHistory(String tenantId, String deviceId, Instant from, Instant to);

    Optional<Location> getLastKnownLocation(String tenantId, String deviceId);

    List<Location> getLocationsNearby(String tenantId, Coordinates center, double radiusKm);

    List<Location> getLocationsInArea(String tenantId, Coordinates southWest, Coordinates northEast);

    Geofence createGeofence(String tenantId, String name, Geofence.GeofenceGeometry geometry,
                            Geofence.GeofenceTransition[] transitions);

    boolean updateGeofence(String tenantId, String geofenceId, Geofence geofence);

    boolean deleteGeofence(String tenantId, String geofenceId);

    List<Geofence> getActiveGeofences(String tenantId);

    List<Geofence> checkGeofences(String tenantId, Location location);

    double calculateDistance(String tenantId, Coordinates from, Coordinates to);

    List<Location> trackDevice(String tenantId, String deviceId, Instant from, Instant to);

    LocationHeatmap generateHeatmap(String tenantId, Coordinates southWest, Coordinates northEast,
                                     int gridSize, Instant from, Instant to);

    GeoLocationStats getStatistics(String tenantId, Instant from, Instant to);

    record LocationHeatmap(
        Coordinates southWest,
        Coordinates northEast,
        int gridSize,
        HeatmapCell[][] cells,
        int totalPoints
    ) {}

    record HeatmapCell(
        double intensity,
        int count,
        Coordinates center
    ) {}

    record GeoLocationStats(
        long totalLocations,
        long uniqueDevices,
        double averageAccuracy,
        List<LocationSourceStats> sourceBreakdown,
        List<CountryStats> topCountries
    ) {}

    record LocationSourceStats(
        Location.LocationSource source,
        long count,
        double percentage
    ) {}

    record CountryStats(
        String country,
        String countryCode,
        long count
    ) {}
}