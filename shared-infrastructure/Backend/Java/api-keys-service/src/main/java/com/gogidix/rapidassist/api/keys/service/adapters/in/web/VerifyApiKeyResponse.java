package com.gogidix.rapidassist.api.keys.service.adapters.in.web;

public record VerifyApiKeyResponse(
        boolean valid,
        String keyId,
        String reason
) {
}
