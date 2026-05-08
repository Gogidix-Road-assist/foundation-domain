package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import java.time.Instant;

public record IntrospectSessionTokenResponse(
        boolean active,
        String subject,
        Instant expiresAt,
        String reason
) {
}
