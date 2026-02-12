package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import java.time.Instant;

public record IssueSessionTokenResponse(
        String token,
        Instant issuedAt,
        Instant expiresAt
) {
}
