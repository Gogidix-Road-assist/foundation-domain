package com.gogidix.rapidassist.orchestration.dispatching.application.service;

import com.gogidix.rapidassist.orchestration.dispatching.application.dto.*;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.*;
import com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final DispatchAssignmentRepository assignmentRepository;
    private final DispatchTrackingRepository trackingRepository;
    private final DispatchProviderRepository providerRepository;
    private final DispatchRouteRepository routeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public DispatchResponse createDispatch(CreateDispatchRequest request) {
        log.info("Creating dispatch for request: {}", request.getRequestId());

        // Build dispatch entity
        Dispatch dispatch = Dispatch.builder()
            .dispatchId(generateDispatchId())
            .requestId(request.getRequestId())
            .tenantId(request.getTenantId())
            .serviceType(request.getServiceType())
            .status(Dispatch.DispatchStatus.PENDING)
            .priority(mapPriority(request.getPriority()))
            .location(mapLocation(request.getLocation()))
            .assignmentMethod(mapAssignmentMethod(request.getAssignmentMethod()))
            .build();

        dispatch = dispatchRepository.save(dispatch);

        // Create tracking event
        createTrackingEvent(dispatch.getDispatchId(), DispatchTracking.TrackingEventType.DISPATCH_CREATED,
            "System", "Dispatch created successfully");

        // Publish Kafka event
        publishDispatchEvent(dispatch, "dispatch.created");

        log.info("Dispatch created successfully with ID: {}", dispatch.getDispatchId());
        return mapToResponse(dispatch);
    }

    @Transactional
    public AssignmentResponse assignProvider(String dispatchId, ProviderAssignmentRequest request) {
        log.info("Assigning provider {} to dispatch: {}", request.getProviderId(), dispatchId);

        Dispatch dispatch = dispatchRepository.findByDispatchId(dispatchId)
            .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

        // Validate provider exists and is available
        DispatchProvider provider = providerRepository.findByProviderId(request.getProviderId())
            .orElseThrow(() -> new RuntimeException("Provider not found: " + request.getProviderId()));

        if (provider.getCurrentStatus() != DispatchProvider.ProviderStatus.AVAILABLE) {
            throw new RuntimeException("Provider is not available: " + request.getProviderId());
        }

        // Create assignment
        DispatchAssignment assignment = DispatchAssignment.builder()
            .dispatchId(dispatchId)
            .providerId(request.getProviderId())
            .vehicleId(request.getVehicleId())
            .driverId(request.getDriverId())
            .status(DispatchAssignment.AssignmentStatus.PENDING)
            .assignedAt(LocalDateTime.now())
            .assignmentScore(request.getAssignmentScore())
            .estimatedDistance(request.getEstimatedDistance())
            .estimatedDuration(request.getEstimatedDuration())
            .tenantId(dispatch.getTenantId())
            .build();

        assignment = assignmentRepository.save(assignment);

        // Update dispatch status
        dispatch.setAssignedProviderId(request.getProviderId());
        dispatch.setAssignedVehicleId(request.getVehicleId());
        dispatch.setStatus(Dispatch.DispatchStatus.ASSIGNED);
        dispatchRepository.save(dispatch);

        // Create tracking event
        createTrackingEvent(dispatchId, DispatchTracking.TrackingEventType.PROVIDER_ASSIGNED,
            request.getProviderId(), "Provider assigned to dispatch");

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.created");

        log.info("Provider assigned successfully to dispatch: {}", dispatchId);
        return mapToAssignmentResponse(assignment);
    }

    @Transactional
    public DispatchResponse updateDispatchStatus(String dispatchId, UpdateDispatchStatusRequest request) {
        log.info("Updating dispatch status: {} to {}", dispatchId, request.getStatus());

        Dispatch dispatch = dispatchRepository.findByDispatchId(dispatchId)
            .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));

        Dispatch.DispatchStatus oldStatus = dispatch.getStatus();
        dispatch.setStatus(mapStatus(request.getStatus()));

        if (request.getActualArrival() != null) {
            dispatch.setActualArrival(request.getActualArrival());
        }

        if (request.getCompletionTime() != null) {
            dispatch.setCompletionTime(request.getCompletionTime());
        }

        dispatch = dispatchRepository.save(dispatch);

        // Create tracking event based on status change
        DispatchTracking.TrackingEventType eventType = mapStatusToEventType(request.getStatus());
        createTrackingEvent(dispatchId, eventType, request.getPerformedBy(),
            request.getNotes() != null ? request.getNotes() : "Status updated");

        // Publish Kafka event
        publishDispatchEvent(dispatch, "dispatch.status.updated");

        log.info("Dispatch status updated: {} from {} to {}", dispatchId, oldStatus, dispatch.getStatus());
        return mapToResponse(dispatch);
    }

    @Transactional
    public AssignmentResponse acceptAssignment(String dispatchId, String providerId) {
        log.info("Provider {} accepting assignment for dispatch: {}", providerId, dispatchId);

        DispatchAssignment assignment = assignmentRepository.findByDispatchIdAndProviderId(dispatchId, providerId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(DispatchAssignment.AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());
        assignment = assignmentRepository.save(assignment);

        // Create tracking event
        createTrackingEvent(dispatchId, DispatchTracking.TrackingEventType.PROVIDER_ACCEPTED,
            providerId, "Provider accepted assignment");

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.accepted");

        return mapToAssignmentResponse(assignment);
    }

    @Transactional
    public AssignmentResponse rejectAssignment(String dispatchId, String providerId, String reason) {
        log.info("Provider {} rejecting assignment for dispatch: {}. Reason: {}", providerId, dispatchId, reason);

        DispatchAssignment assignment = assignmentRepository.findByDispatchIdAndProviderId(dispatchId, providerId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(DispatchAssignment.AssignmentStatus.REJECTED);
        assignment.setRejectedAt(LocalDateTime.now());
        assignment.setRejectionReason(reason);
        assignment = assignmentRepository.save(assignment);

        // Create tracking event
        createTrackingEvent(dispatchId, DispatchTracking.TrackingEventType.PROVIDER_REJECTED,
            providerId, "Provider rejected assignment: " + reason);

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.rejected");

        return mapToAssignmentResponse(assignment);
    }

    public DispatchResponse getDispatch(String dispatchId) {
        Dispatch dispatch = dispatchRepository.findByDispatchId(dispatchId)
            .orElseThrow(() -> new RuntimeException("Dispatch not found: " + dispatchId));
        return mapToResponse(dispatch);
    }

    public List<DispatchResponse> getDispatchesByRequest(String requestId) {
        return dispatchRepository.findByRequestId(requestId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<DispatchResponse> getActiveDispatchesByTenant(String tenantId) {
        return dispatchRepository.findByTenantIdAndStatusIn(
            tenantId,
            List.of(Dispatch.DispatchStatus.PENDING, Dispatch.DispatchStatus.ASSIGNED,
                   Dispatch.DispatchStatus.IN_PROGRESS)
        ).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getAssignmentsByDispatch(String dispatchId) {
        return assignmentRepository.findByDispatchId(dispatchId).stream()
            .map(this::mapToAssignmentResponse)
            .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getProviderAssignments(String providerId) {
        return assignmentRepository.findByProviderIdAndStatusIn(
            providerId,
            List.of(DispatchAssignment.AssignmentStatus.PENDING, DispatchAssignment.AssignmentStatus.ACCEPTED,
                   DispatchAssignment.AssignmentStatus.IN_TRANSIT)
        ).stream()
            .map(this::mapToAssignmentResponse)
            .collect(Collectors.toList());
    }

    public List<DispatchResponse> getProviderActiveDispatches(String providerId) {
        return dispatchRepository.findByAssignedProviderIdAndStatusIn(
            providerId,
            List.of(Dispatch.DispatchStatus.ASSIGNED, Dispatch.DispatchStatus.IN_PROGRESS)
        ).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<DispatchProvider> getAvailableProviders(String tenantId) {
        return providerRepository.findByTenantIdAndCurrentStatus(
            tenantId,
            DispatchProvider.ProviderStatus.AVAILABLE
        );
    }

    @Transactional
    public void updateProviderLocation(String providerId, Double latitude, Double longitude, String address) {
        DispatchProvider.ProviderStatus currentStatus = DispatchProvider.ProviderStatus.AVAILABLE;

        DispatchProvider.CurrentLocation location = DispatchProvider.CurrentLocation.builder()
            .latitude(latitude)
            .longitude(longitude)
            .address(address)
            .timestamp(LocalDateTime.now())
            .build();

        providerRepository.findByProviderId(providerId).ifPresent(provider -> {
            provider.setCurrentLocation(location);
            providerRepository.save(provider);
        });
    }

    private String generateDispatchId() {
        return "DSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void createTrackingEvent(String dispatchId, DispatchTracking.TrackingEventType eventType,
                                     String performedBy, String description) {
        DispatchTracking tracking = DispatchTracking.builder()
            .dispatchId(dispatchId)
            .eventType(eventType)
            .eventDescription(description)
            .performedBy(performedBy)
            .timestamp(LocalDateTime.now())
            .build();

        trackingRepository.save(tracking);
    }

    private Dispatch.DispatchPriority mapPriority(CreateDispatchRequest.DispatchPriorityDTO priority) {
        return Dispatch.DispatchPriority.valueOf(priority.name());
    }

    private Dispatch.DispatchStatus mapStatus(UpdateDispatchStatusRequest.DispatchStatusDTO status) {
        return Dispatch.DispatchStatus.valueOf(status.name());
    }

    private Dispatch.AssignmentMethod mapAssignmentMethod(CreateDispatchRequest.AssignmentMethodDTO method) {
        return method != null ? Dispatch.AssignmentMethod.valueOf(method.name()) : Dispatch.AssignmentMethod.AUTOMATIC;
    }

    private Dispatch.Location mapLocation(CreateDispatchRequest.LocationDTO location) {
        return Dispatch.Location.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    private DispatchResponse mapToResponse(Dispatch dispatch) {
        return DispatchResponse.builder()
            .dispatchId(dispatch.getDispatchId())
            .requestId(dispatch.getRequestId())
            .tenantId(dispatch.getTenantId())
            .serviceType(dispatch.getServiceType())
            .status(mapStatusDTO(dispatch.getStatus()))
            .priority(mapPriorityDTO(dispatch.getPriority()))
            .location(mapLocationDTO(dispatch.getLocation()))
            .assignedProviderId(dispatch.getAssignedProviderId())
            .assignedVehicleId(dispatch.getAssignedVehicleId())
            .estimatedArrival(dispatch.getEstimatedArrival())
            .actualArrival(dispatch.getActualArrival())
            .completionTime(dispatch.getCompletionTime())
            .assignmentMethod(mapAssignmentMethodDTO(dispatch.getAssignmentMethod()))
            .metadata(dispatch.getMetadata())
            .createdAt(dispatch.getCreatedAt())
            .updatedAt(dispatch.getUpdatedAt())
            .build();
    }

    private AssignmentResponse mapToAssignmentResponse(DispatchAssignment assignment) {
        return AssignmentResponse.builder()
            .assignmentId(assignment.getId())
            .dispatchId(assignment.getDispatchId())
            .providerId(assignment.getProviderId())
            .vehicleId(assignment.getVehicleId())
            .driverId(assignment.getDriverId())
            .status(mapAssignmentStatusDTO(assignment.getStatus()))
            .assignedAt(assignment.getAssignedAt())
            .acceptedAt(assignment.getAcceptedAt())
            .assignmentScore(assignment.getAssignmentScore())
            .estimatedDistance(assignment.getEstimatedDistance())
            .estimatedDuration(assignment.getEstimatedDuration())
            .build();
    }

    private DispatchResponse.DispatchStatusDTO mapStatusDTO(Dispatch.DispatchStatus status) {
        return DispatchResponse.DispatchStatusDTO.valueOf(status.name());
    }

    private DispatchResponse.DispatchPriorityDTO mapPriorityDTO(Dispatch.DispatchPriority priority) {
        return DispatchResponse.DispatchPriorityDTO.valueOf(priority.name());
    }

    private DispatchResponse.AssignmentMethodDTO mapAssignmentMethodDTO(Dispatch.AssignmentMethod method) {
        return DispatchResponse.AssignmentMethodDTO.valueOf(method.name());
    }

    private DispatchResponse.LocationDTO mapLocationDTO(Dispatch.Location location) {
        return DispatchResponse.LocationDTO.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    private AssignmentResponse.AssignmentStatusDTO mapAssignmentStatusDTO(DispatchAssignment.AssignmentStatus status) {
        return AssignmentResponse.AssignmentStatusDTO.valueOf(status.name());
    }

    private DispatchTracking.TrackingEventType mapStatusToEventType(UpdateDispatchStatusRequest.DispatchStatusDTO status) {
        return switch (status) {
            case ASSIGNED -> DispatchTracking.TrackingEventType.PROVIDER_ASSIGNED;
            case IN_PROGRESS -> DispatchTracking.TrackingEventType.SERVICE_STARTED;
            case COMPLETED -> DispatchTracking.TrackingEventType.SERVICE_COMPLETED;
            case CANCELLED -> DispatchTracking.TrackingEventType.DISPATCH_CANCELLED;
            case FAILED -> DispatchTracking.TrackingEventType.DISPATCH_FAILED;
            default -> DispatchTracking.TrackingEventType.STATUS_UPDATED;
        };
    }

    private void publishDispatchEvent(Dispatch dispatch, String eventType) {
        try {
            kafkaTemplate.send("dispatch-events", dispatch.getDispatchId(),
                new DispatchEvent(eventType, dispatch.getDispatchId(), dispatch.getTenantId(),
                    dispatch.getStatus().name()));
        } catch (Exception e) {
            log.error("Failed to publish dispatch event: {}", eventType, e);
        }
    }

    private void publishAssignmentEvent(DispatchAssignment assignment, String eventType) {
        try {
            kafkaTemplate.send("assignment-events", assignment.getId(),
                new AssignmentEvent(eventType, assignment.getId(), assignment.getDispatchId(),
                    assignment.getProviderId(), assignment.getStatus().name()));
        } catch (Exception e) {
            log.error("Failed to publish assignment event: {}", eventType, e);
        }
    }

    private record DispatchEvent(String eventType, String dispatchId, String tenantId, String status) {}
    private record AssignmentEvent(String eventType, String assignmentId, String dispatchId,
                                   String providerId, String status) {}
}
