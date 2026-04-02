package com.gogidix.rapidassist.onboarding.service.domain.port.in;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;

public interface GetOnboardingStatusQuery {

    OnboardingRecord get(String tenantId, String subject);
}
