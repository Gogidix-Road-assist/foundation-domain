package com.gogidix.rapidassist.session.token.service.application.usecase;

import com.gogidix.rapidassist.session.token.service.domain.port.in.RevokeSessionTokenCommand;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class RevokeSessionTokenUseCase implements RevokeSessionTokenCommand {

    private final SessionTokenProvider provider;

    public RevokeSessionTokenUseCase(SessionTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public void revoke(String tenantId, String token) {
        provider.revoke(tenantId, token);
    }
}
