package com.gogidix.rapidassist.orchestration.dispatching.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event raised when a provider is assigned to a dispatch
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAssignedEvent extends DispatchEvent {

    private String providerId;
    private String vehicleId;
    private String driverId;
    private Double assignmentScore;

    public ProviderAssignedEvent(String dispatchId, String tenantId, String providerId,
                                String vehicleId, String driverId, Double assignmentScore) {
        super("ProviderAssigned", dispatchId, tenantId);
        this.providerId = providerId;
        this.vehicleId = vehicleId;
        this.driverId = driverId;
        this.assignmentScore = assignmentScore;
    }
}
