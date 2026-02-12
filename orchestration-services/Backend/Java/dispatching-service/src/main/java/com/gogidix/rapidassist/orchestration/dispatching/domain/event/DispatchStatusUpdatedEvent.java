package com.gogidix.rapidassist.orchestration.dispatching.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Event raised when a dispatch status is updated
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchStatusUpdatedEvent extends DispatchEvent {

    private String oldStatus;
    private String newStatus;
    private String performedBy;
    private String notes;

    public DispatchStatusUpdatedEvent(String dispatchId, String tenantId,
                                      String oldStatus, String newStatus,
                                      String performedBy, String notes) {
        super("DispatchStatusUpdated", dispatchId, tenantId);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.performedBy = performedBy;
        this.notes = notes;
    }
}
