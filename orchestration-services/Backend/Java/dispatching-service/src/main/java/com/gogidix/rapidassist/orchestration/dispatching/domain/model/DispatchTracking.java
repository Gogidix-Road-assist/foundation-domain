package com.gogidix.rapidassist.orchestration.dispatching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispatch_tracking")
public class DispatchTracking {

    @Id
    private String id;

    @Indexed
    private String dispatchId;

    @Indexed
    private TrackingEventType eventType;

    private String eventDescription;

    @Indexed
    private String performedBy;

    @Indexed
    private LocalDateTime timestamp;

    private LocationSnapshot locationSnapshot;

    private Map<String, Object> eventData;

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    public enum TrackingEventType {
        DISPATCH_CREATED,
        PROVIDER_ASSIGNED,
        PROVIDER_ACCEPTED,
        PROVIDER_REJECTED,
        PROVIDER_EN_ROUTE,
        PROVIDER_ON_SCENE,
        SERVICE_STARTED,
        SERVICE_COMPLETED,
        DISPATCH_CANCELLED,
        DISPATCH_FAILED,
        STATUS_UPDATED,
        LOCATION_UPDATED,
        ESTIMATED_ARRIVAL_UPDATED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationSnapshot {
        private Double latitude;
        private Double longitude;
        private String address;
        private LocalDateTime capturedAt;
    }
}
