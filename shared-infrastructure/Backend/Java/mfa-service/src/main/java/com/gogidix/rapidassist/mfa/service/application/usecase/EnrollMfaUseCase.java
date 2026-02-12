package com.gogidix.rapidassist.mfa.service.application.usecase;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaEnrollment;
import com.gogidix.rapidassist.mfa.service.domain.port.in.EnrollMfaCommand;
import com.gogidix.rapidassist.mfa.service.domain.port.out.MfaProvider;
import org.springframework.stereotype.Service;

@Service
public class EnrollMfaUseCase implements EnrollMfaCommand {

    private final MfaProvider mfaProvider;

    public EnrollMfaUseCase(MfaProvider mfaProvider) {
        this.mfaProvider = mfaProvider;
    }

    @Override
    public MfaEnrollment enroll(String tenantId, String subject, String method) {
        return mfaProvider.enroll(tenantId, subject, method);
    }
}
