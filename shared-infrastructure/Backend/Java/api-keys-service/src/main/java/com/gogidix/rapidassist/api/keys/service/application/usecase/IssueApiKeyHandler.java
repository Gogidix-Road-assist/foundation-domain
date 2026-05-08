package com.gogidix.rapidassist.api.keys.service.application.usecase;

import com.gogidix.rapidassist.api.keys.service.domain.model.ApiKey;
import com.gogidix.rapidassist.api.keys.service.domain.port.in.IssueApiKeyCommand;
import com.gogidix.rapidassist.api.keys.service.domain.port.out.ApiKeyProvider;
import org.springframework.stereotype.Component;

@Component
public class IssueApiKeyHandler implements IssueApiKeyCommand {

    private final ApiKeyProvider provider;

    public IssueApiKeyHandler(ApiKeyProvider provider) {
        this.provider = provider;
    }

    @Override
    public ApiKey issue(String tenantId, String subject) {
        return provider.issue(tenantId, subject);
    }
}
