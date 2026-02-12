package com.gogidix.rapidassist.session.token.service.application.usecase;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IssueSessionTokenCommand;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class IssueSessionTokenUseCase implements IssueSessionTokenCommand {

    private final SessionTokenProvider provider;

    public IssueSessionTokenUseCase(SessionTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public SessionToken issue(String tenantId, String subject, Duration ttl) {
        return provider.issue(tenantId, subject, ttl);
    }
}
