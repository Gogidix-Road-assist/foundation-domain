package com.gogidix.rapidassist.mfa.service.application.usecase;

import com.gogidix.rapidassist.mfa.service.domain.model.MfaVerificationResult;
import com.gogidix.rapidassist.mfa.service.domain.port.in.VerifyMfaCommand;
import com.gogidix.rapidassist.mfa.service.domain.port.out.MfaProvider;
import org.springframework.stereotype.Service;

@Service
public class VerifyMfaUseCase implements VerifyMfaCommand {

    private final MfaProvider mfaProvider;

    public VerifyMfaUseCase(MfaProvider mfaProvider) {
        this.mfaProvider = mfaProvider;
    }

    @Override
    public MfaVerificationResult verify(String tenantId, String subject, String enrollmentId, String code) {
        return mfaProvider.verify(tenantId, subject, enrollmentId, code);
    }
}
