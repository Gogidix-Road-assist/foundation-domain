package com.gogidix.rapidassist.mfa.service.domain.model;

public record MfaVerificationResult(
        boolean verified,
        String reason
) {
}
