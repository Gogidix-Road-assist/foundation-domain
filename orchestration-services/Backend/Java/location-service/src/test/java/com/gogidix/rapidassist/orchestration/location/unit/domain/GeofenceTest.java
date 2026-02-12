package com.gogidix.rapidassist.orchestration.location.unit.domain;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Geofence domain model
 */
class GeofenceTest {

    @Test
    void testGeofenceCreation() {
        Geofence geofence = Geofence.builder()
                .id("geo123")
                .tenantId("tenant1")
                .name("Warehouse Zone")
                .type(Geofence.GeofenceType.CIRCLE)
                .radius(500.0)
                .status(Geofence.GeofenceStatus.ACTIVE)
                .monitorEntry(true)
                .monitorExit(true)
                .build();

        assertNotNull(geofence);
        assertEquals("geo123", geofence.getId());
        assertEquals("tenant1", geofence.getTenantId());
        assertEquals("Warehouse Zone", geofence.getName());
        assertEquals(Geofence.GeofenceType.CIRCLE, geofence.getType());
        assertEquals(500.0, geofence.getRadius());
        assertEquals(Geofence.GeofenceStatus.ACTIVE, geofence.getStatus());
        assertTrue(geofence.getMonitorEntry());
        assertTrue(geofence.getMonitorExit());
    }

    @Test
    void testIsActive() {
        // Active geofence without expiration
        Geofence activeGeofence = Geofence.builder()
                .status(Geofence.GeofenceStatus.ACTIVE)
                .build();

        assertTrue(activeGeofence.isActive());

        // Active geofence with future expiration
        Geofence activeWithFutureExpiry = Geofence.builder()
                .status(Geofence.GeofenceStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .build();

        assertTrue(activeWithFutureExpiry.isActive());

        // Expired geofence
        Geofence expiredGeofence = Geofence.builder()
                .status(Geofence.GeofenceStatus.ACTIVE)
                .expiresAt(LocalDateTime.now().minusDays(1))
                .build();

        assertFalse(expiredGeofence.isActive());

        // Inactive geofence
        Geofence inactiveGeofence = Geofence.builder()
                .status(Geofence.GeofenceStatus.INACTIVE)
                .build();

        assertFalse(inactiveGeofence.isActive());
    }

    @Test
    void testMonitorsEntityType() {
        Geofence geofence = Geofence.builder()
                .monitoredEntityTypes(List.of("VEHICLE", "DRIVER"))
                .build();

        assertTrue(geofence.monitorsEntityType(Location.EntityType.VEHICLE));
        assertTrue(geofence.monitorsEntityType(Location.EntityType.DRIVER));
        assertFalse(geofence.monitorsEntityType(Location.EntityType.REQUEST));

        // Geofence with null/empty monitored types (monitors all)
        Geofence allMonitorGeofence = Geofence.builder()
                .monitoredEntityTypes(null)
                .build();

        assertTrue(allMonitorGeofence.monitorsEntityType(Location.EntityType.VEHICLE));
        assertTrue(allMonitorGeofence.monitorsEntityType(Location.EntityType.REQUEST));
    }

    @Test
    void testMonitorsEntity() {
        Geofence geofence = Geofence.builder()
                .monitoredEntityIds(List.of("vehicle1", "vehicle2"))
                .build();

        assertTrue(geofence.monitorsEntity("vehicle1"));
        assertTrue(geofence.monitorsEntity("vehicle2"));
        assertFalse(geofence.monitorsEntity("vehicle3"));

        // Geofence with null/empty monitored entities (monitors all)
        Geofence allMonitorGeofence = Geofence.builder()
                .monitoredEntityIds(null)
                .build();

        assertTrue(allMonitorGeofence.monitorsEntity("vehicle1"));
        assertTrue(allMonitorGeofence.monitorsEntity("vehicle999"));
    }

    @Test
    void testGeofenceTypes() {
        assertEquals(2, Geofence.GeofenceType.values().length);
        assertEquals(Geofence.GeofenceType.CIRCLE, Geofence.GeofenceType.valueOf("CIRCLE"));
        assertEquals(Geofence.GeofenceType.POLYGON, Geofence.GeofenceType.valueOf("POLYGON"));
    }

    @Test
    void testGeofenceStatuses() {
        assertEquals(4, Geofence.GeofenceStatus.values().length);
        assertEquals(Geofence.GeofenceStatus.ACTIVE, Geofence.GeofenceStatus.valueOf("ACTIVE"));
        assertEquals(Geofence.GeofenceStatus.INACTIVE, Geofence.GeofenceStatus.valueOf("INACTIVE"));
        assertEquals(Geofence.GeofenceStatus.EXPIRED, Geofence.GeofenceStatus.valueOf("EXPIRED"));
        assertEquals(Geofence.GeofenceStatus.DRAFT, Geofence.GeofenceStatus.valueOf("DRAFT"));
    }
}
