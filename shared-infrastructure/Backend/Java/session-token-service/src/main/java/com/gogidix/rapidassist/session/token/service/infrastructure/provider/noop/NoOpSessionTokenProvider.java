package com.gogidix.rapidassist.session.token.service.infrastructure.provider.noop;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;
import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class NoOpSessionTokenProvider implements SessionTokenProvider {

    @Override
    public SessionToken issue(String tenantId, String subject, Duration ttl) {
        Instant now = Instant.now();
        return new SessionToken(tenantId, subject, UUID.randomUUID().toString(), now, now.plus(ttl));
    }

    @Override
    public SessionIntrospectionResult introspect(String tenantId, String token) {
        return new SessionIntrospectionResult(false, tenantId, null, null, "No session token provider configured");
    }

    @Override
    public void revoke(String tenantId, String token) {
        // no-op
    }
}
