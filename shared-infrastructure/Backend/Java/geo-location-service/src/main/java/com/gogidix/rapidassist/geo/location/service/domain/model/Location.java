package com.gogidix.rapidassist.geo.location.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record Location(
    String locationId,
    String tenantId,
    Coordinates coordinates,
    Address address,
    LocationAccuracy accuracy,
    LocationSource source,
    Instant timestamp,
    String deviceId,
    String userId,
    String ipAddress,
    Map<String, Object> metadata,
    GeofenceStatus geofenceStatus,
    String sessionId
) {

    public static Location fromIp(String tenantId, String ipAddress, Coordinates coordinates, Address address) {
        return new Location(
            generateLocationId(),
            tenantId,
            coordinates,
            address,
            LocationAccuracy.CITY,
            LocationSource.IP_GEOLOCATION,
            Instant.now(),
            null,
            null,
            ipAddress,
            Map.of(),
            GeofenceStatus.UNKNOWN,
            null
        );
    }

    public static Location fromDevice(String tenantId, String deviceId, Coordinates coordinates,
                                    LocationAccuracy accuracy, Address address) {
        return new Location(
            generateLocationId(),
            tenantId,
            coordinates,
            address,
            accuracy,
            LocationSource.GPS,
            Instant.now(),
            deviceId,
            null,
            null,
            Map.of(),
            GeofenceStatus.UNKNOWN,
            null
        );
    }

    public double distanceTo(Location other) {
        return calculateDistance(coordinates(), other.coordinates());
    }

    public boolean isWithinRadius(Location center, double radiusKm) {
        return distanceTo(center) <= radiusKm;
    }

    public String getFullAddress() {
        if (address == null) return null;
        return String.format("%s, %s, %s, %s %s, %s",
            address.streetAddress(),
            address.city(),
            address.state(),
            address.postalCode(),
            address.country(),
            address.countryCode());
    }

    private static String generateLocationId() {
        return "loc_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    private static double calculateDistance(Coordinates coord1, Coordinates coord2) {
        double lat1 = Math.toRadians(coord1.latitude());
        double lon1 = Math.toRadians(coord1.longitude());
        double lat2 = Math.toRadians(coord2.latitude());
        double lon2 = Math.toRadians(coord2.longitude());

        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(lat1) * Math.cos(lat2) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return 6371 * c; // Earth's radius in kilometers
    }

    public enum LocationAccuracy {
        EXACT,         // GPS with high accuracy (<10m)
        PRECISE,       // GPS with good accuracy (<100m)
        APPROXIMATE,   // WiFi/cell tower (~500m)
        NEIGHBORHOOD,  // IP-based (~1-5km)
        CITY,          // City-level (~10-50km)
        REGION,        // Region/state-level
        COUNTRY        // Country-level only
    }

    public enum LocationSource {
        GPS,
        WIFI,
        CELL_TOWER,
        IP_GEOLOCATION,
        MANUAL_ENTRY,
        GEOCODING,
        REVERSE_GEOCODING
    }

    public enum GeofenceStatus {
        INSIDE,
        OUTSIDE,
        ENTERING,
        EXITING,
        UNKNOWN
    }
}