package com.gogidix.rapidassist.onboarding.service.domain.port.in;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;

public interface StartOnboardingCommand {

    OnboardingRecord start(String tenantId, String subject);
}
