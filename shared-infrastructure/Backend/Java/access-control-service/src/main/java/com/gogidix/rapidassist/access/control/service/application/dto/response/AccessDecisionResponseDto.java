package com.gogidix.rapidassist.access.control.service.application.dto.response;

import java.time.Instant;

/**
 * DTO: AccessDecisionResponseDto
 *
 * Response DTO for access control decisions.
 */
public record AccessDecisionResponseDto(
        boolean allowed,
        String reason,
        Instant evaluatedAt,
        String tenantId,
        String subjectId,
        String resource,
        String action
) {
    public static AccessDecisionResponseDto allowed(String reason, String tenantId,
                                                   String subjectId, String resource, String action) {
        return new AccessDecisionResponseDto(true, reason, Instant.now(), tenantId, subjectId, resource, action);
    }

    public static AccessDecisionResponseDto denied(String reason, String tenantId,
                                                  String subjectId, String resource, String action) {
        return new AccessDecisionResponseDto(false, reason, Instant.now(), tenantId, subjectId, resource, action);
    }
}
