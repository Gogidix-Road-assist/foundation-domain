package com.gogidix.rapidassist.session.token.service.domain.port.in;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionIntrospectionResult;

public interface IntrospectSessionTokenQuery {

    SessionIntrospectionResult introspect(String tenantId, String token);
}
