package com.gogidix.rapidassist.orchestration.monitoringservice.application.service;

import com.gogidix.rapidassist.orchestration.monitoringservice.application.dto.*;
import com.gogidix.rapidassist.orchestration.monitoringservice.domain.model.*;
import com.gogidix.rapidassist.orchestration.monitoringservice.domain.repository.EntityRepository;
import com.gogidix.rapidassist.orchestration.monitoringservice.infrastructure.persistence.*;
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
public class EntityService {

    private final EntityRepository entityRepository;
    private final EntityAssignmentRepository assignmentRepository;
    private final EntityTrackingRepository trackingRepository;
    private final EntityProviderRepository providerRepository;
    private final EntityRouteRepository routeRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public EntityResponse createEntity(CreateEntityRequest request) {
        log.info("Creating entity for request: {}", request.getRequestId());

        // Build entity entity
        Entity entity = Entity.builder()
            .entityId(generateEntityId())
            .requestId(request.getRequestId())
            .tenantId(request.getTenantId())
            .serviceType(request.getServiceType())
            .status(Entity.EntityStatus.PENDING)
            .priority(mapPriority(request.getPriority()))
            .location(mapLocation(request.getLocation()))
            .assignmentMethod(mapAssignmentMethod(request.getAssignmentMethod()))
            .build();

        entity = entityRepository.save(entity);

        // Create tracking event
        createTrackingEvent(entity.getEntityId(), EntityTracking.TrackingEventType.DISPATCH_CREATED,
            "System", "Entity created successfully");

        // Publish Kafka event
        publishEntityEvent(entity, "entity.created");

        log.info("Entity created successfully with ID: {}", entity.getEntityId());
        return mapToResponse(entity);
    }

    @Transactional
    public AssignmentResponse assignProvider(String entityId, ProviderAssignmentRequest request) {
        log.info("Assigning provider {} to entity: {}", request.getProviderId(), entityId);

        Entity entity = entityRepository.findByEntityId(entityId)
            .orElseThrow(() -> new RuntimeException("Entity not found: " + entityId));

        // Validate provider exists and is available
        EntityProvider provider = providerRepository.findByProviderId(request.getProviderId())
            .orElseThrow(() -> new RuntimeException("Provider not found: " + request.getProviderId()));

        if (provider.getCurrentStatus() != EntityProvider.ProviderStatus.AVAILABLE) {
            throw new RuntimeException("Provider is not available: " + request.getProviderId());
        }

        // Create assignment
        EntityAssignment assignment = EntityAssignment.builder()
            .entityId(entityId)
            .providerId(request.getProviderId())
            .vehicleId(request.getVehicleId())
            .driverId(request.getDriverId())
            .status(EntityAssignment.AssignmentStatus.PENDING)
            .assignedAt(LocalDateTime.now())
            .assignmentScore(request.getAssignmentScore())
            .estimatedDistance(request.getEstimatedDistance())
            .estimatedDuration(request.getEstimatedDuration())
            .tenantId(entity.getTenantId())
            .build();

        assignment = assignmentRepository.save(assignment);

        // Update entity status
        entity.setAssignedProviderId(request.getProviderId());
        entity.setAssignedVehicleId(request.getVehicleId());
        entity.setStatus(Entity.EntityStatus.ASSIGNED);
        entityRepository.save(entity);

        // Create tracking event
        createTrackingEvent(entityId, EntityTracking.TrackingEventType.PROVIDER_ASSIGNED,
            request.getProviderId(), "Provider assigned to entity");

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.created");

        log.info("Provider assigned successfully to entity: {}", entityId);
        return mapToAssignmentResponse(assignment);
    }

    @Transactional
    public EntityResponse updateEntityStatus(String entityId, UpdateEntityStatusRequest request) {
        log.info("Updating entity status: {} to {}", entityId, request.getStatus());

        Entity entity = entityRepository.findByEntityId(entityId)
            .orElseThrow(() -> new RuntimeException("Entity not found: " + entityId));

        Entity.EntityStatus oldStatus = entity.getStatus();
        entity.setStatus(mapStatus(request.getStatus()));

        if (request.getActualArrival() != null) {
            entity.setActualArrival(request.getActualArrival());
        }

        if (request.getCompletionTime() != null) {
            entity.setCompletionTime(request.getCompletionTime());
        }

        entity = entityRepository.save(entity);

        // Create tracking event based on status change
        EntityTracking.TrackingEventType eventType = mapStatusToEventType(request.getStatus());
        createTrackingEvent(entityId, eventType, request.getPerformedBy(),
            request.getNotes() != null ? request.getNotes() : "Status updated");

        // Publish Kafka event
        publishEntityEvent(entity, "entity.status.updated");

        log.info("Entity status updated: {} from {} to {}", entityId, oldStatus, entity.getStatus());
        return mapToResponse(entity);
    }

    @Transactional
    public AssignmentResponse acceptAssignment(String entityId, String providerId) {
        log.info("Provider {} accepting assignment for entity: {}", providerId, entityId);

        EntityAssignment assignment = assignmentRepository.findByEntityIdAndProviderId(entityId, providerId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(EntityAssignment.AssignmentStatus.ACCEPTED);
        assignment.setAcceptedAt(LocalDateTime.now());
        assignment = assignmentRepository.save(assignment);

        // Create tracking event
        createTrackingEvent(entityId, EntityTracking.TrackingEventType.PROVIDER_ACCEPTED,
            providerId, "Provider accepted assignment");

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.accepted");

        return mapToAssignmentResponse(assignment);
    }

    @Transactional
    public AssignmentResponse rejectAssignment(String entityId, String providerId, String reason) {
        log.info("Provider {} rejecting assignment for entity: {}. Reason: {}", providerId, entityId, reason);

        EntityAssignment assignment = assignmentRepository.findByEntityIdAndProviderId(entityId, providerId)
            .orElseThrow(() -> new RuntimeException("Assignment not found"));

        assignment.setStatus(EntityAssignment.AssignmentStatus.REJECTED);
        assignment.setRejectedAt(LocalDateTime.now());
        assignment.setRejectionReason(reason);
        assignment = assignmentRepository.save(assignment);

        // Create tracking event
        createTrackingEvent(entityId, EntityTracking.TrackingEventType.PROVIDER_REJECTED,
            providerId, "Provider rejected assignment: " + reason);

        // Publish Kafka event
        publishAssignmentEvent(assignment, "assignment.rejected");

        return mapToAssignmentResponse(assignment);
    }

    public EntityResponse getEntity(String entityId) {
        Entity entity = entityRepository.findByEntityId(entityId)
            .orElseThrow(() -> new RuntimeException("Entity not found: " + entityId));
        return mapToResponse(entity);
    }

    public List<EntityResponse> getEntityesByRequest(String requestId) {
        return entityRepository.findByRequestId(requestId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<EntityResponse> getActiveEntityesByTenant(String tenantId) {
        return entityRepository.findByTenantIdAndStatusIn(
            tenantId,
            List.of(Entity.EntityStatus.PENDING, Entity.EntityStatus.ASSIGNED,
                   Entity.EntityStatus.IN_PROGRESS)
        ).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getAssignmentsByEntity(String entityId) {
        return assignmentRepository.findByEntityId(entityId).stream()
            .map(this::mapToAssignmentResponse)
            .collect(Collectors.toList());
    }

    public List<AssignmentResponse> getProviderAssignments(String providerId) {
        return assignmentRepository.findByProviderIdAndStatusIn(
            providerId,
            List.of(EntityAssignment.AssignmentStatus.PENDING, EntityAssignment.AssignmentStatus.ACCEPTED,
                   EntityAssignment.AssignmentStatus.IN_TRANSIT)
        ).stream()
            .map(this::mapToAssignmentResponse)
            .collect(Collectors.toList());
    }

    public List<EntityResponse> getProviderActiveEntityes(String providerId) {
        return entityRepository.findByAssignedProviderIdAndStatusIn(
            providerId,
            List.of(Entity.EntityStatus.ASSIGNED, Entity.EntityStatus.IN_PROGRESS)
        ).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<EntityProvider> getAvailableProviders(String tenantId) {
        return providerRepository.findByTenantIdAndCurrentStatus(
            tenantId,
            EntityProvider.ProviderStatus.AVAILABLE
        );
    }

    @Transactional
    public void updateProviderLocation(String providerId, Double latitude, Double longitude, String address) {
        EntityProvider.Location location = EntityProvider.Location.builder()
            .latitude(latitude)
            .longitude(longitude)
            .address(address)
            .build();

        providerRepository.findByProviderId(providerId).ifPresent(provider -> {
            provider.setCurrentLocation(location);
            providerRepository.save(provider);
        });
    }

    private String generateEntityId() {
        return "DSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void createTrackingEvent(String entityId, EntityTracking.TrackingEventType eventType,
                                     String performedBy, String description) {
        EntityTracking tracking = EntityTracking.builder()
            .entityId(entityId)
            .eventType(eventType)
            .eventDescription(description)
            .performedBy(performedBy)
            .timestamp(LocalDateTime.now())
            .build();

        trackingRepository.save(tracking);
    }

    private Entity.EntityPriority mapPriority(CreateEntityRequest.EntityPriorityDTO priority) {
        return Entity.EntityPriority.valueOf(priority.name());
    }

    private Entity.EntityStatus mapStatus(UpdateEntityStatusRequest.EntityStatusDTO status) {
        return Entity.EntityStatus.valueOf(status.name());
    }

    private EntityResponse.EntityStatusDTO mapStatusDTO(Entity.EntityStatus status) {
        return EntityResponse.EntityStatusDTO.valueOf(status.name());
    }

    private Entity.AssignmentMethod mapAssignmentMethod(CreateEntityRequest.AssignmentMethodDTO method) {
        return method != null ? Entity.AssignmentMethod.valueOf(method.name()) : Entity.AssignmentMethod.AUTOMATIC;
    }

    private Entity.Location mapLocation(CreateEntityRequest.LocationDTO location) {
        return Entity.Location.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    private EntityResponse mapToResponse(Entity entity) {
        return EntityResponse.builder()
            .entityId(entity.getEntityId())
            .requestId(entity.getRequestId())
            .tenantId(entity.getTenantId())
            .serviceType(entity.getServiceType())
            .status(entity.getStatus())
            .priority(entity.getPriority())
            .location(entity.getLocation())
            .assignedProviderId(entity.getAssignedProviderId())
            .assignedVehicleId(entity.getAssignedVehicleId())
            .estimatedArrival(entity.getEstimatedArrival())
            .actualArrival(entity.getActualArrival())
            .completionTime(entity.getCompletionTime())
            .assignmentMethod(entity.getAssignmentMethod())
            .metadata(entity.getMetadata())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private AssignmentResponse mapToAssignmentResponse(EntityAssignment assignment) {
        return AssignmentResponse.builder()
            .assignmentId(assignment.getId())
            .entityId(assignment.getEntityId())
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

    private EntityResponse.EntityPriorityDTO mapPriorityDTO(Entity.EntityPriority priority) {
        return EntityResponse.EntityPriorityDTO.valueOf(priority.name());
    }

    private EntityResponse.AssignmentMethodDTO mapAssignmentMethodDTO(Entity.AssignmentMethod method) {
        return EntityResponse.AssignmentMethodDTO.valueOf(method.name());
    }

    private EntityResponse.LocationDTO mapLocationDTO(Entity.Location location) {
        return EntityResponse.LocationDTO.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    private AssignmentResponse.AssignmentStatusDTO mapAssignmentStatusDTO(EntityAssignment.AssignmentStatus status) {
        return AssignmentResponse.AssignmentStatusDTO.valueOf(status.name());
    }

    private EntityTracking.TrackingEventType mapStatusToEventType(UpdateEntityStatusRequest.EntityStatusDTO status) {
        return switch (status) {
            case ASSIGNED -> EntityTracking.TrackingEventType.PROVIDER_ASSIGNED;
            case IN_PROGRESS -> EntityTracking.TrackingEventType.SERVICE_STARTED;
            case COMPLETED -> EntityTracking.TrackingEventType.SERVICE_COMPLETED;
            case CANCELLED -> EntityTracking.TrackingEventType.DISPATCH_CANCELLED;
            case FAILED -> EntityTracking.TrackingEventType.DISPATCH_FAILED;
            default -> EntityTracking.TrackingEventType.STATUS_UPDATED;
        };
    }

    private void publishEntityEvent(Entity entity, String eventType) {
        try {
            kafkaTemplate.send("entity-events", entity.getEntityId(),
                new EntityEvent(eventType, entity.getEntityId(), entity.getTenantId(),
                    entity.getStatus().name()));
        } catch (Exception e) {
            log.error("Failed to publish entity event: {}", eventType, e);
        }
    }

    private void publishAssignmentEvent(EntityAssignment assignment, String eventType) {
        try {
            kafkaTemplate.send("assignment-events", assignment.getId(),
                new AssignmentEvent(eventType, assignment.getId(), assignment.getEntityId(),
                    assignment.getProviderId(), assignment.getStatus().name()));
        } catch (Exception e) {
            log.error("Failed to publish assignment event: {}", eventType, e);
        }
    }

    private record EntityEvent(String eventType, String entityId, String tenantId, String status) {}
    private record AssignmentEvent(String eventType, String assignmentId, String entityId,
                                   String providerId, String status) {}
}
