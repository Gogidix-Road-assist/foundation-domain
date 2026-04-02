package com.gogidix.rapidassist.ai.gateway.application.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * DTO for RateLimit.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String limitName;
    private String identifier;
    private String limitType;
    private Integer requestsPerWindow;
    private Integer windowSizeSeconds;
    private String algorithm;
    private String status;
    private String scope;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
