package com.gogidix.rapidassist.api.keys.service.domain.port.in;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;

public interface VerifyApiKeyQuery {

    ApiKeyVerificationResult verify(String tenantId, String apiKey);
}
