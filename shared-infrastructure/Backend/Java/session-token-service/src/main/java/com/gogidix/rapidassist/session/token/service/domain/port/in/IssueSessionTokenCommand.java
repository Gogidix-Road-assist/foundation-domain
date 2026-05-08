package com.gogidix.rapidassist.session.token.service.domain.port.in;

import com.gogidix.rapidassist.session.token.service.domain.model.SessionToken;

import java.time.Duration;

public interface IssueSessionTokenCommand {

    SessionToken issue(String tenantId, String subject, Duration ttl);
}
