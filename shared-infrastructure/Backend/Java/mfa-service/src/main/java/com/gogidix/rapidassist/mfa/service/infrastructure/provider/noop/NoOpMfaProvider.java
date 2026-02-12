package com.gogidix.rapidassist.mfa.service.infrastructure.provider.noop;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.domain.port.out.MfaProvider;

import java.time.Instant;
import java.util.UUID;

public class NoOpMfaProvider implements MfaProvider {

    @Override
    public MfaEnrollment enroll(String tenantId, String subject, String method) {
        return new MfaEnrollment(tenantId, subject, method, UUID.randomUUID().toString(), Instant.now());
    }

    @Override
    public MfaVerificationResult verify(String tenantId, String subject, String enrollmentId, String code) {
        return new MfaVerificationResult(false, "No MFA provider configured");
    }
}
