package com.gogidix.rapidassist.ai.anomaly.infrastructure.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT Claims representation.
 * Holds extracted information from validated JWT token.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtClaims {

    private String subject;
    private String tenantId;
    private String userId;
    private String correlationId;
}
