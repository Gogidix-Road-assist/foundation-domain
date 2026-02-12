package com.gogidix.rapidassist.orchestration.dispatching.integration;

import com.gogidix.rapidassist.orchestration.dispatching.application.dto.request.CreateDispatchRequestDto;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.response.DispatchResponseDto;
import com.gogidix.rapidassist.orchestration.dispatching.application.service.DispatchService;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence.DispatchRepository;
import com.gogidix.rapidassist.orchestration.dispatching.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.dispatching.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for ALL services before production
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test_dispatching_db",
    "spring.kafka.auto-startup=false"
})
public class TenantIsolationTest {

    @Autowired
    private DispatchRepository repository;

    @Autowired
    private DispatchService service;

    private RequestContext tenantA;
    private RequestContext tenantB;

    @BeforeEach
    void setUp() {
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

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Create dispatches for both tenants
        RequestContextHolder.set(tenantA);

        CreateDispatchRequestDto requestA = CreateDispatchRequestDto.builder()
            .requestId("REQ-A-001")
            .serviceType("TOWING")
            .priority("HIGH")
            .location(CreateDispatchRequestDto.LocationDto.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .build();

        DispatchResponseDto responseA = service.createDispatch(
            com.gogidix.rapidassist.orchestration.dispatching.application.dto.CreateDispatchRequest.builder()
                .requestId(requestA.getRequestId())
                .tenantId(tenantA.tenantId())
                .serviceType(requestA.getServiceType())
                .priority(CreateDispatchRequest.DispatchPriorityDTO.HIGH)
                .location(CreateDispatchRequest.LocationDTO.builder()
                    .latitude(requestA.getLocation().getLatitude())
                    .longitude(requestA.getLocation().getLongitude())
                    .address(requestA.getLocation().getAddress())
                    .build())
                .assignmentMethod(CreateDispatchRequest.AssignmentMethodDTO.AUTOMATIC)
                .build()
        );

        RequestContextHolder.set(tenantB);

        CreateDispatchRequestDto requestB = CreateDispatchRequestDto.builder()
            .requestId("REQ-B-001")
            .serviceType("TOWING")
            .priority("MEDIUM")
            .location(CreateDispatchRequestDto.LocationDto.builder()
                .latitude(34.0522)
                .longitude(-118.2437)
                .address("Los Angeles, CA")
                .build())
            .build();

        DispatchResponseDto responseB = service.createDispatch(
            com.gogidix.rapidassist.orchestration.dispatching.application.dto.CreateDispatchRequest.builder()
                .requestId(requestB.getRequestId())
                .tenantId(tenantB.tenantId())
                .serviceType(requestB.getServiceType())
                .priority(CreateDispatchRequest.DispatchPriorityDTO.MEDIUM)
                .location(CreateDispatchRequest.LocationDTO.builder()
                    .latitude(requestB.getLocation().getLatitude())
                    .longitude(requestB.getLocation().getLongitude())
                    .address(requestB.getLocation().getAddress())
                    .build())
                .assignmentMethod(CreateDispatchRequest.AssignmentMethodDTO.AUTOMATIC)
                .build()
        );

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(tenantA);
        List<DispatchResponseDto> tenantAResults = service.getActiveDispatchesByTenant(tenantA.tenantId());

        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults.get(0).getRequestId()).isEqualTo("REQ-A-001");
        assertThat(tenantAResults).noneMatch(d -> d.getTenantId().equals("tenant-b"));
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Create dispatch for Tenant A
        RequestContextHolder.set(tenantA);

        CreateDispatchRequestDto requestA = CreateDispatchRequestDto.builder()
            .requestId("REQ-A-002")
            .serviceType("TOWING")
            .priority("HIGH")
            .location(CreateDispatchRequestDto.LocationDto.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .build();

        DispatchResponseDto created = service.createDispatch(
            com.gogidix.rapidassist.orchestration.dispatching.application.dto.CreateDispatchRequest.builder()
                .requestId(requestA.getRequestId())
                .tenantId(tenantA.tenantId())
                .serviceType(requestA.getServiceType())
                .priority(CreateDispatchRequest.DispatchPriorityDTO.HIGH)
                .location(CreateDispatchRequest.LocationDTO.builder()
                    .latitude(requestA.getLocation().getLatitude())
                    .longitude(requestA.getLocation().getLongitude())
                    .address(requestA.getLocation().getAddress())
                    .build())
                .assignmentMethod(CreateDispatchRequest.AssignmentMethodDTO.AUTOMATIC)
                .build()
        );

        String dispatchId = created.getDispatchId();

        // When: Tenant B tries to access same entity
        RequestContextHolder.set(tenantB);

        // Then: Should throw NotFoundException
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class,
            () -> service.getDispatch(dispatchId)
        );
    }

    @Test
    @DisplayName("Direct repository queries are tenant-isolated")
    void whenQueryingDirectly_tenantIsolationEnforced() {
        // Given: Create entities for both tenants
        RequestContextHolder.set(tenantA);

        Dispatch dispatchA = Dispatch.builder()
            .dispatchId("DSP-A-001")
            .requestId("REQ-A")
            .tenantId("tenant-a")
            .serviceType("TOWING")
            .status(Dispatch.DispatchStatus.PENDING)
            .priority(Dispatch.DispatchPriority.HIGH)
            .location(Dispatch.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("NYC")
                .build())
            .build();

        repository.save(dispatchA);

        RequestContextHolder.set(tenantB);

        Dispatch dispatchB = Dispatch.builder()
            .dispatchId("DSP-B-001")
            .requestId("REQ-B")
            .tenantId("tenant-b")
            .serviceType("TOWING")
            .status(Dispatch.DispatchStatus.PENDING)
            .priority(Dispatch.DispatchPriority.MEDIUM)
            .location(Dispatch.Location.builder()
                .latitude(34.0522)
                .longitude(-118.2437)
                .address("LA")
                .build())
            .build();

        repository.save(dispatchB);

        // When: Query from Tenant A context
        RequestContextHolder.set(tenantA);
        List<Dispatch> tenantAResults = repository.findByTenantId("tenant-a");

        // Then: Should only see Tenant A data
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");

        // Cleanup
        repository.deleteById(dispatchA.getId());
        RequestContextHolder.set(tenantB);
        repository.deleteById(dispatchB.getId());
    }
}
