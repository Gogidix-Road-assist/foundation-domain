package com.gogidix.rapidassist.api.keys.service.application.usecase;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKeyVerificationResult;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.VerifyApiKeyQuery;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import org.springframework.stereotype.Component;

@Component
public class VerifyApiKeyHandler implements VerifyApiKeyQuery {

    private final ApiKeyProvider provider;

    public VerifyApiKeyHandler(ApiKeyProvider provider) {
        this.provider = provider;
    }

    @Override
    public ApiKeyVerificationResult verify(String tenantId, String apiKey) {
        return provider.verify(tenantId, apiKey);
    }
}
