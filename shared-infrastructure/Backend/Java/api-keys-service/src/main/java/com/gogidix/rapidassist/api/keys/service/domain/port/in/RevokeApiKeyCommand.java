package com.gogidix.rapidassist.api.keys.service.domain.port.in;

public interface RevokeApiKeyCommand {

    void revoke(String tenantId, String keyId);
}
