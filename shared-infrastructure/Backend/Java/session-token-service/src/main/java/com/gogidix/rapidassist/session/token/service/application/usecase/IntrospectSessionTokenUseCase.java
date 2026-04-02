package com.gogidix.rapidassist.session.token.service.application.usecase;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.port.in.IntrospectSessionTokenQuery;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import org.springframework.stereotype.Service;

@Service
public class IntrospectSessionTokenUseCase implements IntrospectSessionTokenQuery {

    private final SessionTokenProvider provider;

    public IntrospectSessionTokenUseCase(SessionTokenProvider provider) {
        this.provider = provider;
    }

    @Override
    public SessionIntrospectionResult introspect(String tenantId, String token) {
        return provider.introspect(tenantId, token);
    }
}
