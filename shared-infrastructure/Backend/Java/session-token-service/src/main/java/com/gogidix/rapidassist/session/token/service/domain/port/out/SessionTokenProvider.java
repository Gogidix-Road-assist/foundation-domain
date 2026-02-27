package com.gogidix.rapidassist.session.token.service.domain.port.out;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;
import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;

import java.time.Duration;

public interface SessionTokenProvider {

    SessionToken issue(String tenantId, String subject, Duration ttl);

    SessionIntrospectionResult introspect(String tenantId, String token);

    void revoke(String tenantId, String token);
}
