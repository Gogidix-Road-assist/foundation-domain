package com.gogidix.rapidassist.mfa.service.domain.port.in;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;

public interface EnrollMfaCommand {

    MfaEnrollment enroll(String tenantId, String subject, String method);
}
