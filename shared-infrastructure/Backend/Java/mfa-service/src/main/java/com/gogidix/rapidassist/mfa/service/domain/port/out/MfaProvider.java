package com.gogidix.rapidassist.mfa.service.domain.port.out;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;

public interface MfaProvider {

    MfaEnrollment enroll(String tenantId, String subject, String method);

    MfaVerificationResult verify(String tenantId, String subject, String enrollmentId, String code);
}
