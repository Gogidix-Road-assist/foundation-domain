package com.gogidix.rapidassist.orchestration.location.unit.application;

import com.gogidix.rapidassist.orchestration.location.application.service.LocationService;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationHistoryRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka.LocationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LocationService
 */
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepositoryPort locationRepository;

    @Mock
    private LocationHistoryRepositoryPort historyRepository;

    @Mock
    private LocationEventPublisher eventPublisher;

    @InjectMocks
    private LocationService locationService;

    private Location testLocation;

    @BeforeEach
    void setUp() {
        testLocation = Location.builder()
                .id("loc123")
                .tenantId("tenant1")
                .entityType(Location.EntityType.VEHICLE)
                .entityId("vehicle1")
                .latitude(40.7128)
                .longitude(-74.0060)
                .status(Location.LocationStatus.ACTIVE)
                .build();
    }

    @Test
    void testUpdateLocation_NewLocation() {
        when(locationRepository.findByTenantIdAndEntityTypeAndEntityId(any(), any(), any()))
                .thenReturn(Optional.empty());
        when(locationRepository.save(any())).thenReturn(testLocation);

        Location result = locationService.updateLocation(
                "tenant1",
                Location.EntityType.VEHICLE,
                "vehicle1",
                testLocation
        );

        assertNotNull(result);
        assertEquals("tenant1", result.getTenantId());
        assertEquals(Location.EntityType.VEHICLE, result.getEntityType());
        verify(locationRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishLocationUpdated(any());
    }

    @Test
    void testUpdateLocation_ExistingLocation() {
        Location existingLocation = Location.builder()
                .id("loc123")
                .tenantId("tenant1")
                .entityType(Location.EntityType.VEHICLE)
                .entityId("vehicle1")
                .latitude(40.0)
                .longitude(-74.0)
                .build();

        when(locationRepository.findByTenantIdAndEntityTypeAndEntityId(any(), any(), any()))
                .thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(any())).thenReturn(testLocation);
        when(historyRepository.save(any())).thenReturn(any());

        Location result = locationService.updateLocation(
                "tenant1",
                Location.EntityType.VEHICLE,
                "vehicle1",
                testLocation
        );

        assertNotNull(result);
        verify(historyRepository, times(1)).save(any());
        verify(locationRepository, times(1)).save(any());
        verify(eventPublisher, times(1)).publishLocationUpdated(any());
    }

    @Test
    void testGetLocation() {
        when(locationRepository.findByTenantIdAndEntityTypeAndEntityId(any(), any(), any()))
                .thenReturn(Optional.of(testLocation));

        Location result = locationService.getLocation(
                "tenant1",
                Location.EntityType.VEHICLE,
                "vehicle1"
        );

        assertNotNull(result);
        assertEquals("loc123", result.getId());
    }

    @Test
    void testGetLocation_NotFound() {
        when(locationRepository.findByTenantIdAndEntityTypeAndEntityId(any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            locationService.getLocation("tenant1", Location.EntityType.VEHICLE, "vehicle1");
        });
    }

    @Test
    void testFindLocationsWithinRadius() {
        when(locationRepository.findLocationsWithinRadius(any(), any(), any(), any()))
                .thenReturn(List.of(testLocation));

        List<Location> results = locationService.findLocationsWithinRadius(
                "tenant1",
                40.7128,
                -74.0060,
                1000.0
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(locationRepository, times(1))
                .findLocationsWithinRadius(any(), any(), any(), any());
    }

    @Test
    void testBulkUpdateLocations() {
        List<Location> locations = List.of(testLocation);
        when(locationRepository.saveAll(any())).thenReturn(locations);

        List<Location> results = locationService.bulkUpdateLocations("tenant1", locations);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(locationRepository, times(1)).saveAll(any());
        verify(eventPublisher, times(1)).publishBulkLocationsUpdated(any(), any());
    }
}
