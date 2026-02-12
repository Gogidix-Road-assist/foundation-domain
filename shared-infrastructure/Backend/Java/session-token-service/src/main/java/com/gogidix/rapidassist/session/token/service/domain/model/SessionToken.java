package com.gogidix.rapidassist.session.token.service.domain.model;

import java.time.Instant;

public record SessionToken(
        String tenantId,
        String subject,
        String token,
        Instant issuedAt,
        Instant expiresAt
) {
}
