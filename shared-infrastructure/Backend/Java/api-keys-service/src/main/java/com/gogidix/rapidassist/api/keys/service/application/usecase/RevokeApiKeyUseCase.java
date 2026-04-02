package com.gogidix.rapidassist.api.keys.service.application.usecase;

import com.gogidix.rapidassist.api.keys.service.domain.port.in.RevokeApiKeyCommand;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import org.springframework.stereotype.Service;

@Service
public class RevokeApiKeyUseCase implements RevokeApiKeyCommand {

    private final ApiKeyProvider provider;

    public RevokeApiKeyUseCase(ApiKeyProvider provider) {
        this.provider = provider;
    }

    @Override
    public void revoke(String tenantId, String keyId) {
        provider.revoke(tenantId, keyId);
    }
}
