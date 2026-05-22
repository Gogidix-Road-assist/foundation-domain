package com.gogidix.rapidassist.orchestration.countryaggregation.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "domain_health_snapshots")
public class DomainHealthSnapshot {

    @Id
    private String id;

    @Indexed
    private String countryCode;

    @Indexed
    private String domainName;

    private String serviceUrl;
    private HealthStatus status;
    private Integer responseTimeMs;
    private String errorMessage;

    @Builder.Default
    private Map<String, Object> metrics = Map.of();

    @CreatedDate
    @Indexed
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum HealthStatus {
        UP, DOWN, DEGRADED, UNKNOWN
    }
}
