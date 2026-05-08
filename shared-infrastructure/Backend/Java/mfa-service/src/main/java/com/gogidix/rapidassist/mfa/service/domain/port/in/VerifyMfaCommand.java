package com.gogidix.rapidassist.mfa.service.domain.port.in;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;

public interface VerifyMfaCommand {

    MfaVerificationResult verify(String tenantId, String subject, String enrollmentId, String code);
}
