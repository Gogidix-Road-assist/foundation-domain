package com.gogidix.rapidassist.mfa.service.adapters.in.web;

public record VerifyMfaResponse(
        boolean verified,
        String reason
) {
}
