package com.gogidix.rapidassist.api.keys.service.adapters.in.web;

import java.time.Instant;

public record IssueApiKeyResponse(
        String keyId,
        String apiKey,
        Instant issuedAt
) {
}
