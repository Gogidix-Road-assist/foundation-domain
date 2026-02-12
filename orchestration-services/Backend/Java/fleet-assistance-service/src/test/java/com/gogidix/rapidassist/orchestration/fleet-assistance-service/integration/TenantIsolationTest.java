package com.gogidix.rapidassist.orchestration.fleetassistanceservice.integration;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository.FleetRequestRepository;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for production deployment
 */
@SpringBootTest
@ActiveProfiles("test")
class TenantIsolationTest {

    @Autowired
    private FleetRequestRepository repository;

    private RequestContext tenantA;
    private RequestContext tenantB;

    @BeforeEach
    void setUp() {
        // Clean database
        repository.deleteAll();

        // Setup tenants
        tenantA = RequestContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(java.util.UUID.randomUUID().toString())
                .build();

        tenantB = RequestContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(java.util.UUID.randomUUID().toString())
                .build();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Create requests for both tenants
        RequestContextHolder.set(tenantA);

        FleetRequest.Location locationA = FleetRequest.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York")
                .build();

        FleetRequest requestA = FleetRequest.create(
                "tenant-a",
                "fleet-a",
                "TOWING",
                FleetRequest.Priority.HIGH,
                locationA
        );
        repository.save(requestA);

        // Create request for tenant B
        RequestContextHolder.set(tenantB);

        FleetRequest.Location locationB = FleetRequest.Location.builder()
                .latitude(34.0522)
                .longitude(-118.2437)
                .address("Los Angeles")
                .build();

        FleetRequest requestB = FleetRequest.create(
                "tenant-b",
                "fleet-b",
                "TOWING",
                FleetRequest.Priority.MEDIUM,
                locationB
        );
        repository.save(requestB);

        // When: Tenant A queries for requests
        RequestContextHolder.set(tenantA);
        List<FleetRequest> tenantAResults = repository.findByTenantIdAndDeletedAtIsNull("tenant-a");

        // Then: Tenant A should only see their own data
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults.get(0).getFleetId()).isEqualTo("fleet-a");

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Request exists for Tenant A
        RequestContextHolder.set(tenantA);

        FleetRequest.Location location = FleetRequest.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        FleetRequest request = FleetRequest.create(
                "tenant-a",
                "fleet-a",
                "JUMP_START",
                FleetRequest.Priority.EMERGENCY,
                location
        );
        FleetRequest saved = repository.save(request);
        String requestId = saved.getRequestId();

        // When: Tenant B tries to access same request
        RequestContextHolder.set(tenantB);

        var result = repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(requestId, "tenant-b");

        // Then: Result should be empty
        assertThat(result).isEmpty();

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Fleet isolation - queries by fleet ID respect tenant boundary")
    void whenQueryingByFleetId_shouldRespectTenantBoundary() {
        // Given: Both tenants have fleets with same ID
        RequestContextHolder.set(tenantA);

        FleetRequest.Location location = FleetRequest.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .build();

        FleetRequest requestA = FleetRequest.create(
                "tenant-a",
                "fleet-1",
                "TIRE_CHANGE",
                FleetRequest.Priority.LOW,
                location
        );
        repository.save(requestA);

        RequestContextHolder.set(tenantB);

        FleetRequest requestB = FleetRequest.create(
                "tenant-b",
                "fleet-1",
                "FUEL_DELIVERY",
                FleetRequest.Priority.MEDIUM,
                location
        );
        repository.save(requestB);

        // When: Tenant A queries by fleet ID
        RequestContextHolder.set(tenantA);
        List<FleetRequest> tenantAFleet1 = repository.findByFleetIdAndTenantIdAndDeletedAtIsNull("fleet-1", "tenant-a");

        // Then: Should only get tenant A's request
        assertThat(tenantAFleet1).hasSize(1);
        assertThat(tenantAFleet1.get(0).getServiceType()).isEqualTo("TIRE_CHANGE");

        // Cleanup
        RequestContextHolder.clear();
    }
}
