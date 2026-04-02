package com.gogidix.rapidassist.api.keys.service.domain.port.out;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;

public interface ApiKeyProvider {

    ApiKey issue(String tenantId, String subject);

    ApiKeyVerificationResult verify(String tenantId, String apiKey);

    void revoke(String tenantId, String keyId);
}
