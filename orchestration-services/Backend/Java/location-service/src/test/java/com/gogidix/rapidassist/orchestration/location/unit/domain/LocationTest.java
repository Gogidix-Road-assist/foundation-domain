package com.gogidix.rapidassist.orchestration.location.unit.domain;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Location domain model
 */
class LocationTest {

    @Test
    void testLocationCreation() {
        Location location = Location.builder()
                .id("loc123")
                .tenantId("tenant1")
                .entityType(Location.EntityType.VEHICLE)
                .entityId("vehicle1")
                .latitude(40.7128)
                .longitude(-74.0060)
                .status(Location.LocationStatus.ACTIVE)
                .build();

        assertNotNull(location);
        assertEquals("loc123", location.getId());
        assertEquals("tenant1", location.getTenantId());
        assertEquals(Location.EntityType.VEHICLE, location.getEntityType());
        assertEquals("vehicle1", location.getEntityId());
        assertEquals(40.7128, location.getLatitude());
        assertEquals(-74.0060, location.getLongitude());
        assertEquals(Location.LocationStatus.ACTIVE, location.getStatus());
    }

    @Test
    void testUpdateCoordinates() {
        Location location = Location.builder()
                .id("loc123")
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        location.updateCoordinates(34.0522, -118.2437);

        assertEquals(34.0522, location.getLatitude());
        assertEquals(-118.2437, location.getLongitude());
        assertNotNull(location.getCoordinates());
        assertNotNull(location.getLastUpdated());
    }

    @Test
    void testIsWithinRadius() {
        Location location = Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        // Test within 1000 meters
        assertTrue(location.isWithinRadius(40.7128, -74.0060, 1000.0));

        // Test outside 1000 meters
        assertFalse(location.isWithinRadius(41.0, -75.0, 1000.0));
    }

    @Test
    void testCalculateDistance() {
        Location location = Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        // Distance to same point should be 0
        double distance = location.calculateDistance(
                40.7128, -74.0060,
                40.7128, -74.0060
        );
        assertEquals(0.0, distance, 0.01);

        // Distance to different point (NYC to LA is approximately 3944 km)
        double distanceToLA = location.calculateDistance(
                40.7128, -74.0060,
                34.0522, -118.2437
        );
        assertTrue(distanceToLA > 3900000); // Greater than 3900 km
        assertTrue(distanceToLA < 4000000); // Less than 4000 km
    }

    @Test
    void testEntityTypes() {
        assertEquals(4, Location.EntityType.values().length);
        assertEquals(Location.EntityType.VEHICLE, Location.EntityType.valueOf("VEHICLE"));
        assertEquals(Location.EntityType.DRIVER, Location.EntityType.valueOf("DRIVER"));
        assertEquals(Location.EntityType.REQUEST, Location.EntityType.valueOf("REQUEST"));
        assertEquals(Location.EntityType.PROVIDER, Location.EntityType.valueOf("PROVIDER"));
    }

    @Test
    void testLocationStatuses() {
        assertEquals(6, Location.LocationStatus.values().length);
        assertEquals(Location.LocationStatus.ACTIVE, Location.LocationStatus.valueOf("ACTIVE"));
        assertEquals(Location.LocationStatus.INACTIVE, Location.LocationStatus.valueOf("INACTIVE"));
        assertEquals(Location.LocationStatus.MOVING, Location.LocationStatus.valueOf("MOVING"));
        assertEquals(Location.LocationStatus.IDLE, Location.LocationStatus.valueOf("IDLE"));
        assertEquals(Location.LocationStatus.OFFLINE, Location.LocationStatus.valueOf("OFFLINE"));
        assertEquals(Location.LocationStatus.ERROR, Location.LocationStatus.valueOf("ERROR"));
    }
}
