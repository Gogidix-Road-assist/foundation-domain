package com.gogidix.rapidassist.orchestration.dispatching.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dispatch_metrics")
@CompoundIndex(name = "idx_provider_date", def = "{'providerId': 1, 'date': -1}")
public class DispatchMetrics {

    @Id
    private String id;

    @Indexed
    private String providerId;

    @Indexed
    private LocalDate date;

    @Indexed
    private String tenantId;

    // Performance metrics
    private Integer totalDispatches;
    private Integer acceptedDispatches;
    private Integer rejectedDispatches;
    private Integer completedDispatches;
    private Integer cancelledDispatches;

    private Double acceptanceRate;
    private Double completionRate;
    private Double averageResponseTime; // in minutes
    private Double averageServiceTime; // in minutes

    // Distance metrics
    private Double totalDistance;
    private Double averageDistance;

    // Rating metrics
    private Double averageRating;
    private Integer totalRatings;

    // Time metrics
    private Double averageOnTimeToScene; // in minutes
    private Double averageEnRouteTime; // in minutes

    @Builder.Default
    private Map<String, Object> metadata = Map.of();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
