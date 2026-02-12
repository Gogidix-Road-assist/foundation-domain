package com.gogidix.rapidassist.orchestration.dispatching.domain.aggregate;

import java.time.LocalDateTime;

import com.gogidix.rapidassist.orchestration.dispatching.domain.event.DispatchCreatedEvent;
import com.gogidix.rapidassist.orchestration.dispatching.domain.event.DispatchStatusUpdatedEvent;
import com.gogidix.rapidassist.orchestration.dispatching.domain.event.ProviderAssignedEvent;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dispatch Aggregate Root
 * Manages the dispatch lifecycle and enforces business rules
 */
@Getter
public class DispatchAggregate {

    private final Dispatch dispatch;
    private final List<Object> domainEvents = new ArrayList<>();

    public DispatchAggregate(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    /**
     * Create a new dispatch aggregate
     */
    public static DispatchAggregate create(String tenantId, String requestId, String serviceType,
                                          Dispatch.DispatchPriority priority, Dispatch.Location location) {
        Dispatch dispatch = Dispatch.builder()
            .dispatchId(generateDispatchId())
            .requestId(requestId)
            .tenantId(tenantId)
            .serviceType(serviceType)
            .status(Dispatch.DispatchStatus.PENDING)
            .priority(priority)
            .location(location)
            .assignmentMethod(Dispatch.AssignmentMethod.AUTOMATIC)
            .build();

        DispatchAggregate aggregate = new DispatchAggregate(dispatch);
        aggregate.registerEvent(new DispatchCreatedEvent(
            dispatch.getDispatchId(),
            tenantId,
            requestId,
            serviceType,
            priority.name(),
            location.getLatitude(),
            location.getLongitude(),
            location.getAddress()
        ));

        return aggregate;
    }

    /**
     * Assign a provider to this dispatch
     */
    public void assignProvider(String providerId, String vehicleId, String driverId, Double score) {
        if (dispatch.getStatus() != Dispatch.DispatchStatus.PENDING) {
            throw new IllegalStateException("Cannot assign provider to dispatch with status: " + dispatch.getStatus());
        }

        dispatch.setAssignedProviderId(providerId);
        dispatch.setAssignedVehicleId(vehicleId);
        dispatch.setStatus(Dispatch.DispatchStatus.ASSIGNED);

        registerEvent(new ProviderAssignedEvent(
            dispatch.getDispatchId(),
            dispatch.getTenantId(),
            providerId,
            vehicleId,
            driverId,
            score
        ));
    }

    /**
     * Update dispatch status
     */
    public void updateStatus(Dispatch.DispatchStatus newStatus, String performedBy, String notes) {
        Dispatch.DispatchStatus oldStatus = dispatch.getStatus();
        dispatch.setStatus(newStatus);

        registerEvent(new DispatchStatusUpdatedEvent(
            dispatch.getDispatchId(),
            dispatch.getTenantId(),
            oldStatus.name(),
            newStatus.name(),
            performedBy,
            notes
        ));
    }

    /**
     * Mark dispatch as completed
     */
    public void complete(String performedBy) {
        if (dispatch.getStatus() != Dispatch.DispatchStatus.IN_PROGRESS) {
            throw new IllegalStateException("Cannot complete dispatch with status: " + dispatch.getStatus());
        }

        updateStatus(Dispatch.DispatchStatus.COMPLETED, performedBy, "Dispatch completed successfully");
        dispatch.setCompletionTime(java.time.LocalDateTime.now());
    }

    /**
     * Cancel dispatch
     */
    public void cancel(String performedBy, String reason) {
        if (dispatch.getStatus() == Dispatch.DispatchStatus.COMPLETED ||
            dispatch.getStatus() == Dispatch.DispatchStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel dispatch with status: " + dispatch.getStatus());
        }

        updateStatus(Dispatch.DispatchStatus.CANCELLED, performedBy, reason);
    }

    /**
     * Get all domain events
     */
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    /**
     * Clear domain events after publishing
     */
    public void clearDomainEvents() {
        domainEvents.clear();
    }

    private void registerEvent(Object event) {
        domainEvents.add(event);
    }

    private static String generateDispatchId() {
        return "DSP-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
