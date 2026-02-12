package com.gogidix.rapidassist.orchestration.fleetassistanceservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

/**
 * Historical record of all fleet assistance requests
 * MongoDB Collection: assistance_history
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "assistance_history")
@TypeAlias("assistance_history")
public class AssistanceHistory {

    @Id
    private String id;

    @Indexed
    @Field("request_id")
    private String requestId;

    @Indexed
    @Field("fleet_id")
    private String fleetId;

    @Field("vehicle_id")
    private String vehicleId;

    @Field("service_type")
    private String serviceType;

    @Field("timestamp")
    private Instant timestamp;

    @Field("action")
    private String action;

    @Field("provider_id")
    private String providerId;

    @Field("duration_minutes")
    private Integer durationMinutes;

    @Field("cost")
    private Double cost;

    @Field("status")
    private String status;

    @Field("metadata")
    private Map<String, Object> metadata;

    @Indexed
    @Field("tenant_id")
    private String tenantId;

    @Field("created_at")
    private Instant createdAt;

    /**
     * Domain logic: Create history entry
     */
    public static AssistanceHistory create(String tenantId, String requestId, String fleetId, String serviceType, String action) {
        return AssistanceHistory.builder()
                .tenantId(tenantId)
                .requestId(requestId)
                .fleetId(fleetId)
                .serviceType(serviceType)
                .action(action)
                .timestamp(Instant.now())
                .createdAt(Instant.now())
                .build();
    }

    /**
     * Domain logic: Add completion details
     */
    public void addCompletionDetails(String providerId, Integer durationMinutes, Double cost) {
        this.providerId = providerId;
        this.durationMinutes = durationMinutes;
        this.cost = cost;
        this.status = "COMPLETED";
    }
}
