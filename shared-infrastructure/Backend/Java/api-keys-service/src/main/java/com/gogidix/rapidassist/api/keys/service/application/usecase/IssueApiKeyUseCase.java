package com.gogidix.rapidassist.api.keys.service.application.usecase;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.IssueApiKeyCommand;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import org.springframework.stereotype.Service;

@Service
public class IssueApiKeyUseCase implements IssueApiKeyCommand {

    private final ApiKeyProvider provider;

    public IssueApiKeyUseCase(ApiKeyProvider provider) {
        this.provider = provider;
    }

    @Override
    public ApiKey issue(String tenantId, String subject) {
        return provider.issue(tenantId, subject);
    }
}
