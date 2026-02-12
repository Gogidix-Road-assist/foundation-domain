package com.gogidix.rapidassist.session.token.service.domain.port.in;

public interface RevokeSessionTokenCommand {

    void revoke(String tenantId, String token);
}
