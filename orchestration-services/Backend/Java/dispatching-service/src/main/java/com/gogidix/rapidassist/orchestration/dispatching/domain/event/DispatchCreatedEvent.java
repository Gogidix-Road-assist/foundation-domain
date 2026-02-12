package com.gogidix.rapidassist.orchestration.dispatching.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event raised when a dispatch is created
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchCreatedEvent extends DispatchEvent {

    private String requestId;
    private String serviceType;
    private String priority;
    private Double latitude;
    private Double longitude;
    private String address;

    public DispatchCreatedEvent(String dispatchId, String tenantId, String requestId,
                               String serviceType, String priority, Double latitude,
                               Double longitude, String address) {
        super("DispatchCreated", dispatchId, tenantId);
        this.requestId = requestId;
        this.serviceType = serviceType;
        this.priority = priority;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
