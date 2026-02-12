package com.gogidix.rapidassist.orchestration.location.unit.infrastructure;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage.LocationRepositoryAdapter;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.LocationMongoRepository;
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
 * Unit tests for LocationRepositoryAdapter
 */
@ExtendWith(MockitoExtension.class)
class LocationRepositoryAdapterTest {

    @Mock
    private LocationMongoRepository mongoRepository;

    @InjectMocks
    private LocationRepositoryAdapter adapter;

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
    void testSave() {
        when(mongoRepository.save(any())).thenReturn(testLocation);

        Location result = adapter.save(testLocation);

        assertNotNull(result);
        assertEquals("loc123", result.getId());
        verify(mongoRepository, times(1)).save(any());
    }

    @Test
    void testFindById() {
        when(mongoRepository.findById("loc123")).thenReturn(Optional.of(testLocation));

        Optional<Location> result = adapter.findById("loc123");

        assertTrue(result.isPresent());
        assertEquals("loc123", result.get().getId());
    }

    @Test
    void testFindByTenantIdAndEntityTypeAndEntityId() {
        when(mongoRepository.findByTenantIdAndEntityTypeAndEntityId(
                "tenant1", Location.EntityType.VEHICLE, "vehicle1"
        )).thenReturn(Optional.of(testLocation));

        Optional<Location> result = adapter.findByTenantIdAndEntityTypeAndEntityId(
                "tenant1", Location.EntityType.VEHICLE, "vehicle1"
        );

        assertTrue(result.isPresent());
        assertEquals("vehicle1", result.get().getEntityId());
    }

    @Test
    void testFindLocationsWithinRadius() {
        when(mongoRepository.findLocationsWithinRadius(
                any(), any(), any(), any()
        )).thenReturn(List.of(testLocation));

        List<Location> results = adapter.findLocationsWithinRadius(
                "tenant1", 40.7128, -74.0060, 1000.0
        );

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(mongoRepository, times(1))
                .findLocationsWithinRadius(any(), any(), any(), any());
    }

    @Test
    void testSaveAll() {
        List<Location> locations = List.of(testLocation);
        when(mongoRepository.saveAll(any())).thenReturn(locations);

        List<Location> results = adapter.saveAll(locations);

        assertNotNull(results);
        assertEquals(1, results.size());
        verify(mongoRepository, times(1)).saveAll(any());
    }
}
