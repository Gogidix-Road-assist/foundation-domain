package com.gogidix.rapidassist.api.keys.service.domain.model;

import java.time.Instant;

public record ApiKey(
        String tenantId,
        String keyId,
        String key,
        Instant issuedAt
) {
}
