package com.gogidix.rapidassist.api.keys.service.domain.model;

public record ApiKeyVerificationResult(
        boolean valid,
        String keyId,
        String reason
) {
}
