package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * MongoDB Document for RequestLog.
 * Maps to request_log collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "request_log")
public class RequestLogEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed
    private String requestId;

    @Indexed
    private String routeId;

    @Indexed
    private String serviceId;

    private String httpMethod;
    private String requestPath;
    private String clientIp;
    @Indexed
    private String apiKey;
    @Indexed
    private String userId;
    @Indexed
    private Integer statusCode;
    private Long responseTimeMs;
    private Long requestSizeBytes;
    private Long responseSizeBytes;
    private String userAgent;
    private Map<String, String> requestHeaders;
    private Map<String, String> responseHeaders;
    private String errorMessage;
    @Indexed
    private Boolean success;
    @Indexed
    private LocalDateTime createdAt;
}
