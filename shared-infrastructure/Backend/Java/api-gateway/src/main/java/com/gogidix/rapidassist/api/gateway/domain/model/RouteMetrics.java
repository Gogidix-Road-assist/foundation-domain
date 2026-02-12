package com.gogidix.rapidassist.api.gateway.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Document(collection = "route_metrics")
public record RouteMetrics(

    @Id
    String id,

    @Field("tenantId")
    String tenantId,

    @Field("routeId")
    String routeId,

    @Field("timestamp")
    Instant timestamp,

    @Field("totalRequests")
    long totalRequests,

    @Field("successfulRequests")
    long successfulRequests,

    @Field("failedRequests")
    long failedRequests,

    @Field("avgResponseTime")
    double avgResponseTime,

    @Field("minResponseTime")
    double minResponseTime,

    @Field("maxResponseTime")
    double maxResponseTime,

    @Field("p95ResponseTime")
    double p95ResponseTime,

    @Field("p99ResponseTime")
    double p99ResponseTime,

    @Field("requestsPerSecond")
    double requestsPerSecond,

    @Field("errorRate")
    double errorRate,

    @Field("statusCodes")
    Map<Integer, Long> statusCodes,

    @Field("tags")
    Map<String, String> tags

) {

    public static RouteMetrics create(
        String tenantId, String routeId, long totalRequests,
        long successfulRequests, long failedRequests
    ) {
        return new RouteMetrics(
            null, tenantId, routeId, Instant.now(),
            totalRequests, successfulRequests, failedRequests,
            0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
            Map.of(), Map.of()
        );
    }
}
