package com.gogidix.rapidassist.geo.location.service.application.service;

import com.gogidix.rapidassist.geo.location.service.domain.model.*;
import com.gogidix.rapidassist.geo.location.service.domain.port.in.GeoLocationCommand;
import com.gogidix.rapidassist.geo.location.service.domain.port.out.*;
import com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb.GeofenceRepository;
import com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb.LocationRepository;
import com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb.LocationDocument;
import com.gogidix.rapidassist.geo.location.service.infrastructure.provider.MaxMindGeoLocationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class ComprehensiveGeoLocationService implements GeoLocationCommand {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveGeoLocationService.class);

    private final LocationStore locationStore;
    private final GeofenceStore geofenceStore;
    private final List<GeocodingProvider> geocodingProviders;
    private final MaxMindGeoLocationProvider maxMindProvider;

    // Cache for recent geocoding results
    private final Map<String, Coordinates> geocodingCache = new ConcurrentHashMap<>();
    private final Map<String, List<Address>> reverseGeocodingCache = new ConcurrentHashMap<>();

    public ComprehensiveGeoLocationService(LocationStore locationStore,
                                         GeofenceStore geofenceStore,
                                         List<GeocodingProvider> geocodingProviders,
                                         MaxMindGeoLocationProvider maxMindProvider) {
        this.locationStore = locationStore;
        this.geofenceStore = geofenceStore;
        this.geocodingProviders = geocodingProviders;
        this.maxMindProvider = maxMindProvider;
    }

    @Override
    public Location storeLocation(String tenantId, Coordinates coordinates, Location.LocationAccuracy accuracy,
                               String deviceId, String userId, Location.LocationSource source) {
        logger.debug("Storing location: tenant={}, deviceId={}, accuracy={}", tenantId, deviceId, accuracy);

        Location location = new Location(
            UUID.randomUUID().toString(),
            tenantId,
            coordinates,
            null, // Address will be filled by reverse geocoding if available
            accuracy,
            source,
            Instant.now(),
            deviceId,
            userId,
            null, // IP address
            Map.of(),
            Location.GeofenceStatus.UNKNOWN,
            null
        );

        // Trigger reverse geocoding asynchronously
        reverseGeocodeCoordinates(tenantId, location);

        return locationStore.save(location);
    }

    @Override
    public Location storeLocationFromIp(String tenantId, String ipAddress) {
        logger.debug("Looking up location from IP: tenant={}, ip={}", tenantId, ipAddress);

        // Try MaxMind first
        Optional<Location> optLocation = maxMindProvider.getLocationFromIp(tenantId, ipAddress);

        if (optLocation.isPresent()) {
            Location location = locationStore.save(optLocation.get());
            logger.debug("Location found via MaxMind: {}", location.locationId());
            return location;
        }

        // Create a basic location record if MaxMind fails
        Location location = new Location(
            UUID.randomUUID().toString(),
            tenantId,
            Coordinates.of(0, 0), // Default coordinates
            null,
            Location.LocationAccuracy.COUNTRY,
            Location.LocationSource.IP_GEOLOCATION,
            Instant.now(),
            null,
            null,
            ipAddress,
            Map.of("error", "MaxMind lookup failed"),
            Location.GeofenceStatus.UNKNOWN,
            null
        );

        return locationStore.save(location);
    }

    @Override
    public Location geocodeAddress(String tenantId, String address) {
        logger.debug("Geocoding address: tenant={}, address={}", tenantId, address);

        // Check cache first
        if (geocodingCache.containsKey(address)) {
            Coordinates cached = geocodingCache.get(address);
            Location location = Location.fromIp(tenantId, null, cached, null);
            return locationStore.save(location);
        }

        for (GeocodingProvider provider : geocodingProviders) {
            if (!provider.isAvailable()) {
                continue;
            }

            try {
                CompletableFuture<Coordinates> future = provider.geocodeToCoordinates(address);
                Coordinates coordinates = future.get();

                if (coordinates != null) {
                    // Cache the result
                    geocodingCache.put(address, coordinates);

                    Location location = Location.fromIp(tenantId, null, coordinates, null);
                    return locationStore.save(location);
                }
            } catch (Exception e) {
                logger.warn("Geocoding failed with provider: {}", provider.getClass().getSimpleName(), e);
            }
        }

        logger.warn("All geocoding providers failed for address: {}", address);
        return null;
    }

    @Override
    public List<Location> reverseGeocode(String tenantId, Coordinates coordinates) {
        logger.debug("Reverse geocoding coordinates: tenant={}, lat={}, lon={}",
            tenantId, coordinates.latitude(), coordinates.longitude());

        String key = String.format("%.6f,%.6f", coordinates.latitude(), coordinates.longitude());

        // Check cache first
        if (reverseGeocodingCache.containsKey(key)) {
            List<Address> cached = reverseGeocodingCache.get(key);
            return cached.stream()
                .map(address -> Location.fromIp(tenantId, null, coordinates, address))
                .toList();
        }

        for (GeocodingProvider provider : geocodingProviders) {
            if (!provider.isAvailable()) {
                continue;
            }

            try {
                CompletableFuture<List<Address>> future = provider.reverseGeocode(coordinates);
                List<Address> addresses = future.get();

                if (!addresses.isEmpty()) {
                    // Cache the result
                    reverseGeocodingCache.put(key, addresses);

                    return addresses.stream()
                        .map(address -> Location.fromIp(tenantId, null, coordinates, address))
                        .toList();
                }
            } catch (Exception e) {
                logger.warn("Reverse geocoding failed with provider: {}", provider.getClass().getSimpleName(), e);
            }
        }

        logger.debug("No addresses found for coordinates: {}", coordinates);
        return List.of();
    }

    @Override
    public List<Location> getLocationHistory(String tenantId, String deviceId, Instant from, Instant to) {
        logger.debug("Getting location history: tenant={}, deviceId={}, from={}, to={}",
            tenantId, deviceId, from, to);

        return locationStore.findByDeviceIdAndTimeRange(tenantId, deviceId, from, to);
    }

    @Override
    public Optional<Location> getLastKnownLocation(String tenantId, String deviceId) {
        logger.debug("Getting last known location: tenant={}, deviceId={}", tenantId, deviceId);

        List<Location> locations = locationStore.findByDeviceId(tenantId, deviceId);
        return locations.stream()
            .max(Comparator.comparing(Location::timestamp))
            .map(Optional::of)
            .orElse(Optional.empty());
    }

    @Override
    public List<Location> getLocationsNearby(String tenantId, Coordinates center, double radiusKm) {
        logger.debug("Getting nearby locations: tenant={}, center={}, radius={}km",
            tenantId, center, radiusKm);

        // Convert radius to meters for MongoDB query
        double radiusMeters = radiusKm * 1000;

        // For simplicity, we'll use a bounding box approximation
        double latDegree = radiusKm / 111.0; // Approximate
        double lonDegree = radiusKm / (111.0 * Math.cos(Math.toRadians(center.latitude())));

        List<Location> allLocations = locationStore.findByTenantId(tenantId);
        return allLocations.stream()
            .filter(location -> {
                double distance = calculateDistance(center, location.coordinates());
                return distance <= radiusKm;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Location> getLocationsInArea(String tenantId, Coordinates southWest, Coordinates northEast) {
        logger.debug("Getting locations in area: tenant={}, sw={}, ne={}",
            tenantId, southWest, northEast);

        return locationStore.findByTenantId(tenantId).stream()
            .filter(location -> {
                Coordinates coord = location.coordinates();
                return coord.latitude() >= southWest.latitude() &&
                       coord.latitude() <= northEast.latitude() &&
                       coord.longitude() >= southWest.longitude() &&
                       coord.longitude() <= northEast.longitude();
            })
            .collect(Collectors.toList());
    }

    @Override
    public Geofence createGeofence(String tenantId, String name, Geofence.GeofenceGeometry geometry,
                                 Geofence.GeofenceTransition[] transitions) {
        logger.debug("Creating geofence: tenant={}, name={}, type={}", tenantId, name, geometry.getClass().getSimpleName());

        Geofence geofence = new Geofence(
            UUID.randomUUID().toString(),
            tenantId,
            name,
            determineGeofenceType(geometry),
            geometry,
            transitions,
            Map.of(),
            true,
            Instant.now(),
            Instant.now(),
            "system"
        );

        return geofenceStore.save(geofence);
    }

    @Override
    public boolean updateGeofence(String tenantId, String geofenceId, Geofence geofence) {
        logger.debug("Updating geofence: tenant={}, geofenceId={}", tenantId, geofenceId);

        Optional<Geofence> existing = geofenceStore.find(tenantId, geofenceId);

        if (existing.isEmpty()) {
            return false;
        }

        Geofence updated = new Geofence(
            geofenceId,
            tenantId,
            geofence.name(),
            geofence.type(),
            geofence.geometry(),
            geofence.transitions(),
            geofence.metadata(),
            geofence.isActive(),
            existing.get().createdAt(),
            Instant.now(),
            existing.get().createdBy()
        );

        geofenceStore.save(updated);
        return true;
    }

    @Override
    public boolean deleteGeofence(String tenantId, String geofenceId) {
        logger.debug("Deleting geofence: tenant={}, geofenceId={}", tenantId, geofenceId);
        geofenceStore.delete(tenantId, geofenceId);
        return true;
    }

    @Override
    public List<Geofence> getActiveGeofences(String tenantId) {
        logger.debug("Getting active geofences: tenant={}", tenantId);
        return geofenceStore.findActiveByTenantId(tenantId);
    }

    @Override
    public List<Geofence> checkGeofences(String tenantId, Location location) {
        logger.debug("Checking geofences for location: tenant={}, lat={}, lon={}",
            tenantId, location.coordinates().latitude(), location.coordinates().longitude());

        List<Geofence> allGeofences = geofenceStore.findActiveByTenantId(tenantId);

        return allGeofences.stream()
            .filter(geofence -> {
                boolean contains = geofence.containsPoint(location.coordinates());
                if (contains) {
                    logger.debug("Location inside geofence: {}", geofence.geofenceId());
                }
                return contains;
            })
            .collect(Collectors.toList());
    }

    @Override
    public double calculateDistance(String tenantId, Coordinates from, Coordinates to) {
        return calculateDistance(from, to);
    }

    @Override
    public List<Location> trackDevice(String tenantId, String deviceId, Instant from, Instant to) {
        logger.debug("Tracking device: tenant={}, deviceId={}, from={}, to={}",
            tenantId, deviceId, from, to);
        return getLocationHistory(tenantId, deviceId, from, to);
    }

    @Override
    public LocationHeatmap generateHeatmap(String tenantId, Coordinates southWest, Coordinates northEast,
                                            int gridSize, Instant from, Instant to) {
        logger.debug("Generating heatmap: tenant={}, gridSize={}", tenantId, gridSize);

        List<Location> locations = locationStore.findByTimeRange(tenantId, from, to)
            .stream()
            .filter(loc -> isWithinBounds(loc.coordinates(), southWest, northEast))
            .collect(Collectors.toList());

        GeoLocationCommand.HeatmapCell[][] cells = new GeoLocationCommand.HeatmapCell[gridSize][gridSize];
        double latStep = (northEast.latitude() - southWest.latitude()) / gridSize;
        double lonStep = (northEast.longitude() - southWest.longitude()) / gridSize;

        // Initialize cells
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                double centerLat = southWest.latitude() + (i + 0.5) * latStep;
                double centerLon = southWest.longitude() + (j + 0.5) * lonStep;
                cells[i][j] = new GeoLocationCommand.HeatmapCell(0, 0,
                    Coordinates.of(centerLat, centerLon));
            }
        }

        // Count locations in each cell
        for (Location location : locations) {
            Coordinates coord = location.coordinates();
            int latIndex = (int) ((coord.latitude() - southWest.latitude()) / latStep);
            int lonIndex = (int) ((coord.longitude() - southWest.longitude()) / lonStep);

            if (latIndex >= 0 && latIndex < gridSize && lonIndex >= 0 && lonIndex < gridSize) {
                GeoLocationCommand.HeatmapCell cell = cells[latIndex][lonIndex];
                cells[latIndex][lonIndex] = new GeoLocationCommand.HeatmapCell(
                    cell.intensity() + 1,
                    cell.count() + 1,
                    cell.center()
                );
            }
        }

        // Normalize intensity
        int maxCount = locations.stream()
            .mapToInt(loc -> 1)
            .max()
            .orElse(1);

        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                GeoLocationCommand.HeatmapCell cell = cells[i][j];
                double normalizedIntensity = (double) cell.count() / maxCount;
                cells[i][j] = new GeoLocationCommand.HeatmapCell(
                    normalizedIntensity,
                    cell.count(),
                    cell.center()
                );
            }
        }

        return new LocationHeatmap(southWest, northEast, gridSize, cells, locations.size());
    }

    @Override
    public GeoLocationStats getStatistics(String tenantId, Instant from, Instant to) {
        logger.debug("Getting statistics: tenant={}, from={}, to={}", tenantId, from, to);

        List<Location> locations = locationStore.findByTimeRange(tenantId, from, to);

        Map<Location.LocationSource, Long> sourceCount = locations.stream()
            .collect(Collectors.groupingBy(Location::source, Collectors.counting()));

        Map<String, Long> countryCount = locations.stream()
            .filter(loc -> loc.address() != null && loc.address().countryCode() != null)
            .collect(Collectors.groupingBy(
                loc -> loc.address().countryCode(),
                Collectors.counting()
            ));

        List<CountryStats> topCountries = countryCount.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
            .limit(10)
            .map(entry -> new CountryStats(
                entry.getKey(),
                entry.getKey(),
                entry.getValue()
            ))
            .collect(Collectors.toList());

        List<LocationSourceStats> sourceBreakdown = sourceCount.entrySet().stream()
            .map(entry -> {
                double percentage = (double) entry.getValue() / locations.size() * 100;
                return new LocationSourceStats(entry.getKey(), entry.getValue(), percentage);
            })
            .collect(Collectors.toList());

        Set<String> uniqueDeviceIds = locations.stream()
            .map(Location::deviceId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        double avgAccuracy = locations.stream()
            .filter(loc -> loc.coordinates().accuracy() != null)
            .mapToDouble(loc -> loc.coordinates().accuracy())
            .average()
            .orElse(0.0);

        return new GeoLocationStats(
            locations.size(),
            uniqueDeviceIds.size(),
            avgAccuracy,
            sourceBreakdown,
            topCountries
        );
    }

    // Scheduled tasks
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupOldData() {
        logger.debug("Cleaning up old location data");
        // Remove locations older than 1 year
        Instant cutoff = Instant.now().minusSeconds(365 * 24 * 60 * 60);
        // Implementation would depend on store capabilities
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void refreshGeocodingCaches() {
        // Clean up expired cache entries
        geocodingCache.entrySet().removeIf(entry -> {
            // Remove entries older than 1 hour
            return false; // For now, keep all entries
        });
    }

    // Helper methods
    private void reverseGeocodeCoordinates(String tenantId, Location location) {
        CompletableFuture.runAsync(() -> {
            List<Location> locationsWithAddress = reverseGeocode(tenantId, location.coordinates());
            if (!locationsWithAddress.isEmpty() && locationsWithAddress.get(0).address() != null
                && !locationsWithAddress.get(0).address().formattedAddress().isEmpty()) {
                // Update location with address
                Location updated = new Location(
                    location.locationId(),
                    tenantId,
                    location.coordinates(),
                    locationsWithAddress.get(0).address(),
                    location.accuracy(),
                    location.source(),
                    location.timestamp(),
                    location.deviceId(),
                    location.userId(),
                    location.ipAddress(),
                    location.metadata(),
                    location.geofenceStatus(),
                    location.sessionId()
                );
                locationStore.save(updated);
            }
        });
    }

    private Geofence.GeofenceType determineGeofenceType(Geofence.GeofenceGeometry geometry) {
        if (geometry instanceof Geofence.GeofenceCircle) {
            return Geofence.GeofenceType.CIRCLE;
        } else if (geometry instanceof Geofence.GeofencePolygon) {
            return Geofence.GeofenceType.POLYGON;
        } else if (geometry instanceof Geofence.GeofenceRectangle) {
            return Geofence.GeofenceType.RECTANGLE;
        }
        throw new IllegalArgumentException("Unknown geofence geometry type: " + geometry.getClass());
    }

    private double calculateDistance(Coordinates coord1, Coordinates coord2) {
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

    private boolean isWithinBounds(Coordinates coord, Coordinates southWest, Coordinates northEast) {
        return coord.latitude() >= southWest.latitude() &&
               coord.latitude() <= northEast.latitude() &&
               coord.longitude() >= southWest.longitude() &&
               coord.longitude() <= northEast.longitude();
    }
}