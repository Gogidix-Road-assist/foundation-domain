package com.gogidix.rapidassist.api.keys.service.infrastructure.provider.noop;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;

import java.time.Instant;
import java.util.UUID;

public class NoOpApiKeyProvider implements ApiKeyProvider {

    @Override
    public ApiKey issue(String tenantId, String subject) {
        return new ApiKey(tenantId, UUID.randomUUID().toString(), UUID.randomUUID().toString(), Instant.now());
    }

    @Override
    public ApiKeyVerificationResult verify(String tenantId, String apiKey) {
        return new ApiKeyVerificationResult(false, null, "No API key provider configured");
    }

    @Override
    public void revoke(String tenantId, String keyId) {
        // no-op
    }
}
