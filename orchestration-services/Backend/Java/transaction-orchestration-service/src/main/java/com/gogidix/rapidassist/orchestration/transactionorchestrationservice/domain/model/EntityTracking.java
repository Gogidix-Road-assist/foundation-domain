package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "entity_tracking")
public class EntityTracking {

    @Id
    private String id;

    @Indexed
    private String entityId;

    @Indexed
    private TrackingEventType eventType;

    private String eventDescription;
    private String performedBy;
    private String reason;

    private LocalDateTime timestamp;

    @Indexed
    private String tenantId;

    public enum TrackingEventType {
        DISPATCH_CREATED,
        PROVIDER_ASSIGNED,
        DISPATCH_ACCEPTED,
        DISPATCH_REJECTED,
        DISPATCH_STARTED,
        DISPATCH_COMPLETED,
        SERVICE_STARTED,
        SERVICE_COMPLETED,
        PROVIDER_ACCEPTED,
        PROVIDER_REJECTED,
        DISPATCH_CANCELLED,
        DISPATCH_FAILED,
        STATUS_UPDATED,
        PROVIDER_LOCATION_UPDATED
    }
}
