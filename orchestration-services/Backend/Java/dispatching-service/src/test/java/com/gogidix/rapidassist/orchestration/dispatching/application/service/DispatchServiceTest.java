package com.gogidix.rapidassist.orchestration.dispatching.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.*;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.*;
import com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private DispatchRepository dispatchRepository;

    @Mock
    private DispatchAssignmentRepository assignmentRepository;

    @Mock
    private DispatchTrackingRepository trackingRepository;

    @Mock
    private DispatchProviderRepository providerRepository;

    @Mock
    private DispatchRouteRepository routeRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private DispatchService dispatchService;

    private Dispatch testDispatch;
    private DispatchProvider testProvider;
    private CreateDispatchRequest createRequest;

    @BeforeEach
    void setUp() {
        createTestDispatch();
        createTestProvider();
        createTestRequest();
    }

    private void createTestDispatch() {
        testDispatch = Dispatch.builder()
            .id("id1")
            .dispatchId("DSP-12345678")
            .requestId("REQ-001")
            .tenantId("tenant-001")
            .serviceType("TOWING")
            .status(Dispatch.DispatchStatus.PENDING)
            .priority(Dispatch.DispatchPriority.HIGH)
            .location(Dispatch.Location.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .assignmentMethod(Dispatch.AssignmentMethod.AUTOMATIC)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private void createTestProvider() {
        testProvider = DispatchProvider.builder()
            .id("provider-id")
            .providerId("PROV-001")
            .tenantId("tenant-001")
            .userId("user-001")
            .currentStatus(DispatchProvider.ProviderStatus.AVAILABLE)
            .currentLoad(0)
            .maxConcurrentJobs(5)
            .averageRating(4.5)
            .totalCompletedJobs(100)
            .build();
    }

    private void createTestRequest() {
        createRequest = CreateDispatchRequest.builder()
            .requestId("REQ-001")
            .tenantId("tenant-001")
            .serviceType("TOWING")
            .priority(CreateDispatchRequest.DispatchPriorityDTO.HIGH)
            .location(CreateDispatchRequest.LocationDTO.builder()
                .latitude(40.7128)
                .longitude(-74.0060)
                .address("New York, NY")
                .build())
            .assignmentMethod(CreateDispatchRequest.AssignmentMethodDTO.AUTOMATIC)
            .build();
    }

    @Test
    void createDispatch_ShouldCreateDispatchSuccessfully() {
        // Given
        when(dispatchRepository.save(any(Dispatch.class))).thenReturn(testDispatch);
        when(trackingRepository.save(any(DispatchTracking.class))).thenReturn(mock(DispatchTracking.class));

        // When
        DispatchResponse response = dispatchService.createDispatch(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRequestId()).isEqualTo("REQ-001");
        assertThat(response.getTenantId()).isEqualTo("tenant-001");
        assertThat(response.getServiceType()).isEqualTo("TOWING");
        assertThat(response.getStatus()).isEqualTo(DispatchResponse.DispatchStatusDTO.PENDING);

        verify(dispatchRepository, times(1)).save(any(Dispatch.class));
        verify(trackingRepository, times(1)).save(any(DispatchTracking.class));
        verify(kafkaTemplate, times(1)).send(eq("dispatch-events"), anyString(), any());
    }

    @Test
    void getDispatch_ShouldReturnDispatchWhenExists() {
        // Given
        when(dispatchRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(Optional.of(testDispatch));

        // When
        DispatchResponse response = dispatchService.getDispatch("DSP-12345678");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDispatchId()).isEqualTo("DSP-12345678");
        assertThat(response.getRequestId()).isEqualTo("REQ-001");

        verify(dispatchRepository, times(1)).findByDispatchId("DSP-12345678");
    }

    @Test
    void getDispatch_ShouldThrowExceptionWhenNotFound() {
        // Given
        when(dispatchRepository.findByDispatchId("DSP-NOTFOUND"))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> dispatchService.getDispatch("DSP-NOTFOUND"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Dispatch not found");

        verify(dispatchRepository, times(1)).findByDispatchId("DSP-NOTFOUND");
    }

    @Test
    void getDispatchesByRequest_ShouldReturnListOfDispatches() {
        // Given
        when(dispatchRepository.findByRequestId("REQ-001"))
            .thenReturn(List.of(testDispatch));

        // When
        List<DispatchResponse> responses = dispatchService.getDispatchesByRequest("REQ-001");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getRequestId()).isEqualTo("REQ-001");

        verify(dispatchRepository, times(1)).findByRequestId("REQ-001");
    }

    @Test
    void getActiveDispatchesByTenant_ShouldReturnActiveDispatches() {
        // Given
        when(dispatchRepository.findByTenantIdAndStatusIn(eq("tenant-001"), anyList()))
            .thenReturn(List.of(testDispatch));

        // When
        List<DispatchResponse> responses = dispatchService.getActiveDispatchesByTenant("tenant-001");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTenantId()).isEqualTo("tenant-001");

        verify(dispatchRepository, times(1)).findByTenantIdAndStatusIn(eq("tenant-001"), anyList());
    }

    @Test
    void assignProvider_ShouldAssignProviderSuccessfully() {
        // Given
        when(dispatchRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(Optional.of(testDispatch));
        when(providerRepository.findByProviderId("PROV-001"))
            .thenReturn(Optional.of(testProvider));

        DispatchAssignment assignment = DispatchAssignment.builder()
            .id("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(DispatchAssignment.AssignmentStatus.PENDING)
            .assignedAt(LocalDateTime.now())
            .tenantId("tenant-001")
            .build();

        when(assignmentRepository.save(any(DispatchAssignment.class))).thenReturn(assignment);
        when(dispatchRepository.save(any(Dispatch.class))).thenReturn(testDispatch);
        when(trackingRepository.save(any(DispatchTracking.class))).thenReturn(mock(DispatchTracking.class));

        ProviderAssignmentRequest request = ProviderAssignmentRequest.builder()
            .providerId("PROV-001")
            .assignmentScore(95.0)
            .estimatedDistance(5000.0)
            .estimatedDuration(1800)
            .build();

        // When
        AssignmentResponse response = dispatchService.assignProvider("DSP-12345678", request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getProviderId()).isEqualTo("PROV-001");
        assertThat(response.getDispatchId()).isEqualTo("DSP-12345678");

        verify(dispatchRepository, times(1)).findByDispatchId("DSP-12345678");
        verify(providerRepository, times(1)).findByProviderId("PROV-001");
        verify(assignmentRepository, times(1)).save(any(DispatchAssignment.class));
        verify(dispatchRepository, times(1)).save(any(Dispatch.class));
    }

    @Test
    void assignProvider_ShouldThrowExceptionWhenProviderNotFound() {
        // Given
        when(dispatchRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(Optional.of(testDispatch));
        when(providerRepository.findByProviderId("PROV-NOTFOUND"))
            .thenReturn(Optional.empty());

        ProviderAssignmentRequest request = ProviderAssignmentRequest.builder()
            .providerId("PROV-NOTFOUND")
            .assignmentScore(95.0)
            .build();

        // When & Then
        assertThatThrownBy(() -> dispatchService.assignProvider("DSP-12345678", request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Provider not found");

        verify(providerRepository, times(1)).findByProviderId("PROV-NOTFOUND");
        verify(assignmentRepository, never()).save(any(DispatchAssignment.class));
    }

    @Test
    void assignProvider_ShouldThrowExceptionWhenProviderNotAvailable() {
        // Given
        testProvider.setCurrentStatus(DispatchProvider.ProviderStatus.BUSY);
        when(dispatchRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(Optional.of(testDispatch));
        when(providerRepository.findByProviderId("PROV-001"))
            .thenReturn(Optional.of(testProvider));

        ProviderAssignmentRequest request = ProviderAssignmentRequest.builder()
            .providerId("PROV-001")
            .assignmentScore(95.0)
            .build();

        // When & Then
        assertThatThrownBy(() -> dispatchService.assignProvider("DSP-12345678", request))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Provider is not available");

        verify(assignmentRepository, never()).save(any(DispatchAssignment.class));
    }

    @Test
    void updateDispatchStatus_ShouldUpdateStatusSuccessfully() {
        // Given
        when(dispatchRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(Optional.of(testDispatch));
        when(dispatchRepository.save(any(Dispatch.class))).thenReturn(testDispatch);
        when(trackingRepository.save(any(DispatchTracking.class))).thenReturn(mock(DispatchTracking.class));

        UpdateDispatchStatusRequest request = UpdateDispatchStatusRequest.builder()
            .status(UpdateDispatchStatusRequest.DispatchStatusDTO.IN_PROGRESS)
            .performedBy("user-001")
            .notes("Started service")
            .build();

        // When
        DispatchResponse response = dispatchService.updateDispatchStatus("DSP-12345678", request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(DispatchResponse.DispatchStatusDTO.IN_PROGRESS);

        verify(dispatchRepository, times(1)).save(any(Dispatch.class));
        verify(trackingRepository, times(1)).save(any(DispatchTracking.class));
    }

    @Test
    void acceptAssignment_ShouldAcceptSuccessfully() {
        // Given
        DispatchAssignment assignment = DispatchAssignment.builder()
            .id("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(DispatchAssignment.AssignmentStatus.PENDING)
            .assignedAt(LocalDateTime.now())
            .tenantId("tenant-001")
            .build();

        when(assignmentRepository.findByDispatchIdAndProviderId("DSP-12345678", "PROV-001"))
            .thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(DispatchAssignment.class))).thenReturn(assignment);
        when(trackingRepository.save(any(DispatchTracking.class))).thenReturn(mock(DispatchTracking.class));

        // When
        AssignmentResponse response = dispatchService.acceptAssignment("DSP-12345678", "PROV-001");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AssignmentResponse.AssignmentStatusDTO.ACCEPTED);

        verify(assignmentRepository, times(1)).save(any(DispatchAssignment.class));
        verify(trackingRepository, times(1)).save(any(DispatchTracking.class));
    }

    @Test
    void rejectAssignment_ShouldRejectSuccessfully() {
        // Given
        DispatchAssignment assignment = DispatchAssignment.builder()
            .id("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(DispatchAssignment.AssignmentStatus.PENDING)
            .assignedAt(LocalDateTime.now())
            .tenantId("tenant-001")
            .build();

        when(assignmentRepository.findByDispatchIdAndProviderId("DSP-12345678", "PROV-001"))
            .thenReturn(Optional.of(assignment));
        when(assignmentRepository.save(any(DispatchAssignment.class))).thenReturn(assignment);
        when(trackingRepository.save(any(DispatchTracking.class))).thenReturn(mock(DispatchTracking.class));

        // When
        AssignmentResponse response = dispatchService.rejectAssignment("DSP-12345678", "PROV-001", "Too far");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(AssignmentResponse.AssignmentStatusDTO.REJECTED);

        verify(assignmentRepository, times(1)).save(any(DispatchAssignment.class));
        verify(trackingRepository, times(1)).save(any(DispatchTracking.class));
    }

    @Test
    void getAvailableProviders_ShouldReturnAvailableProviders() {
        // Given
        when(providerRepository.findByTenantIdAndCurrentStatus("tenant-001",
            DispatchProvider.ProviderStatus.AVAILABLE))
            .thenReturn(List.of(testProvider));

        // When
        List<DispatchProvider> providers = dispatchService.getAvailableProviders("tenant-001");

        // Then
        assertThat(providers).hasSize(1);
        assertThat(providers.get(0).getProviderId()).isEqualTo("PROV-001");
        assertThat(providers.get(0).getCurrentStatus()).isEqualTo(DispatchProvider.ProviderStatus.AVAILABLE);

        verify(providerRepository, times(1))
            .findByTenantIdAndCurrentStatus("tenant-001", DispatchProvider.ProviderStatus.AVAILABLE);
    }

    @Test
    void updateProviderLocation_ShouldUpdateLocationSuccessfully() {
        // Given
        when(providerRepository.findByProviderId("PROV-001"))
            .thenReturn(Optional.of(testProvider));
        when(providerRepository.save(any(DispatchProvider.class))).thenReturn(testProvider);

        // When
        dispatchService.updateProviderLocation("PROV-001", 40.7589, -73.9851, "Times Square, NYC");

        // Then
        verify(providerRepository, times(1)).findByProviderId("PROV-001");
        verify(providerRepository, times(1)).save(any(DispatchProvider.class));
    }

    @Test
    void getAssignmentsByDispatch_ShouldReturnAssignments() {
        // Given
        DispatchAssignment assignment = DispatchAssignment.builder()
            .id("assignment-id")
            .dispatchId("DSP-12345678")
            .providerId("PROV-001")
            .status(DispatchAssignment.AssignmentStatus.ACCEPTED)
            .tenantId("tenant-001")
            .build();

        when(assignmentRepository.findByDispatchId("DSP-12345678"))
            .thenReturn(List.of(assignment));

        // When
        List<AssignmentResponse> responses = dispatchService.getAssignmentsByDispatch("DSP-12345678");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getDispatchId()).isEqualTo("DSP-12345678");

        verify(assignmentRepository, times(1)).findByDispatchId("DSP-12345678");
    }

    @Test
    void getProviderActiveDispatches_ShouldReturnActiveDispatches() {
        // Given
        when(dispatchRepository.findByAssignedProviderIdAndStatusIn(eq("PROV-001"), anyList()))
            .thenReturn(List.of(testDispatch));

        // When
        List<DispatchResponse> responses = dispatchService.getProviderActiveDispatches("PROV-001");

        // Then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAssignedProviderId()).isEqualTo("PROV-001");

        verify(dispatchRepository, times(1)).findByAssignedProviderIdAndStatusIn(eq("PROV-001"), anyList());
    }
}
