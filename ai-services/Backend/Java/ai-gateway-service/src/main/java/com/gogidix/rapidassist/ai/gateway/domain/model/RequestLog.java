package com.gogidix.rapidassist.ai.gateway.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing Request Log.
 * Pure domain model without MongoDB annotations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestLog {

    private UUID id;
    private String tenantId;
    private String requestId;
    private String routeId;
    private String serviceId;
    private String httpMethod;
    private String requestPath;
    private String clientIp;
    private String apiKey;
    private String userId;
    private Integer statusCode;
    private Long responseTimeMs;
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private String userAgent;
    private java.util.Map<String, String> requestHeaders;
    private java.util.Map<String, String> responseHeaders;
    private String errorMessage;
    private Boolean success;
    private LocalDateTime createdAt;
}
