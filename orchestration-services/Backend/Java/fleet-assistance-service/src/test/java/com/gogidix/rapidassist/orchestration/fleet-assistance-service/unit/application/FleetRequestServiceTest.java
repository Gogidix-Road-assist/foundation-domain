package com.gogidix.rapidassist.orchestration.fleetassistanceservice.unit.application;

import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.request.CreateFleetRequestDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.dto.response.FleetRequestResponseDto;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.mapper.FleetRequestMapper;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.application.service.FleetRequestService;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model.FleetRequest;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository.AssistanceHistoryRepository;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.repository.FleetRequestRepository;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.exception.NotFoundException;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleetassistanceservice.shared.requestcontext.RequestContextHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FleetRequestService
 */
@ExtendWith(MockitoExtension.class)
class FleetRequestServiceTest {

    @Mock
    private FleetRequestRepository repository;

    @Mock
    private AssistanceHistoryRepository historyRepository;

    @Mock
    private FleetRequestMapper mapper;

    @InjectMocks
    private FleetRequestService service;

    private RequestContext testContext;
    private FleetRequest testRequest;
    private FleetRequestResponseDto testResponse;

    @BeforeEach
    void setUp() {
        testContext = RequestContext.builder()
                .tenantId("test-tenant")
                .userId("test-user")
                .correlationId("test-correlation")
                .build();

        RequestContextHolder.set(testContext);

        // Create test request
        FleetRequest.Location location = FleetRequest.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York")
                .build();

        testRequest = FleetRequest.create(
                "test-tenant",
                "fleet-1",
                "TOWING",
                FleetRequest.Priority.HIGH,
                location
        );

        testResponse = FleetRequestResponseDto.builder()
                .requestId(testRequest.getRequestId())
                .fleetId(testRequest.getFleetId())
                .serviceType(testRequest.getServiceType())
                .status(testRequest.getStatus())
                .priority(testRequest.getPriority())
                .tenantId(testRequest.getTenantId())
                .build();
    }

    @Test
    @DisplayName("Should create fleet request successfully")
    void createRequest_shouldReturnResponseDto() {
        // Given
        CreateFleetRequestDto dto = CreateFleetRequestDto.builder()
                .fleetId("fleet-1")
                .serviceType("TOWING")
                .priority(FleetRequest.Priority.HIGH)
                .location(CreateFleetRequestDto.LocationDto.builder()
                        .latitude(40.7128)
                        .longitude(-74.0060)
                        .address("New York")
                        .build())
                .build();

        when(repository.save(any(FleetRequest.class))).thenReturn(testRequest);
        when(mapper.toResponseDto(any(FleetRequest.class))).thenReturn(testResponse);

        // When
        FleetRequestResponseDto response = service.createRequest(dto);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getFleetId()).isEqualTo("fleet-1");
        assertThat(response.getServiceType()).isEqualTo("TOWING");
        verify(repository).save(any(FleetRequest.class));
        verify(historyRepository).save(any());
    }

    @Test
    @DisplayName("Should get request by ID")
    void getRequest_whenExists_shouldReturnResponseDto() {
        // Given
        when(repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(
                testRequest.getRequestId(), "test-tenant"))
                .thenReturn(Optional.of(testRequest));
        when(mapper.toResponseDto(testRequest)).thenReturn(testResponse);

        // When
        FleetRequestResponseDto response = service.getRequest(testRequest.getRequestId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRequestId()).isEqualTo(testRequest.getRequestId());
        verify(repository).findByRequestIdAndTenantIdAndDeletedAtIsNull(
                testRequest.getRequestId(), "test-tenant");
    }

    @Test
    @DisplayName("Should throw NotFoundException when request doesn't exist")
    void getRequest_whenNotExists_shouldThrowNotFoundException() {
        // Given
        String nonExistentRequestId = "non-existent";
        when(repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(
                nonExistentRequestId, "test-tenant"))
                .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> service.getRequest(nonExistentRequestId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Fleet request not found");
    }

    @Test
    @DisplayName("Should assign provider to request")
    void assignProvider_shouldUpdateRequestStatus() {
        // Given
        String providerId = "provider-1";
        when(repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(
                testRequest.getRequestId(), "test-tenant"))
                .thenReturn(Optional.of(testRequest));
        when(repository.save(any(FleetRequest.class))).thenReturn(testRequest);
        when(mapper.toResponseDto(any(FleetRequest.class))).thenReturn(testResponse);

        // When
        FleetRequestResponseDto response = service.assignProvider(testRequest.getRequestId(), providerId);

        // Then
        assertThat(response).isNotNull();
        assertThat(testRequest.getStatus()).isEqualTo(FleetRequest.RequestStatus.ASSIGNED);
        assertThat(testRequest.getAssignedFleetProviderId()).isEqualTo(providerId);
        verify(repository).save(testRequest);
    }

    @Test
    @DisplayName("Should complete service")
    void completeService_shouldMarkAsCompleted() {
        // Given
        testRequest.assignProvider("provider-1", java.time.Instant.now());
        when(repository.findByRequestIdAndTenantIdAndDeletedAtIsNull(
                testRequest.getRequestId(), "test-tenant"))
                .thenReturn(Optional.of(testRequest));
        when(repository.save(any(FleetRequest.class))).thenReturn(testRequest);
        when(mapper.toResponseDto(any(FleetRequest.class))).thenReturn(testResponse);

        // When
        service.startService(testRequest.getRequestId());
        FleetRequestResponseDto response = service.completeService(testRequest.getRequestId());

        // Then
        assertThat(testRequest.getStatus()).isEqualTo(FleetRequest.RequestStatus.COMPLETED);
        assertThat(testRequest.getCompletionTime()).isNotNull();
        verify(repository, atLeastOnce()).save(testRequest);
    }
}
