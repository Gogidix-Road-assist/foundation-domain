package com.gogidix.rapidassist.onboarding.service.application.usecase;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.GetOnboardingStatusQuery;
import com.gogidix.rapidassist.onboarding.service.domain.port.out.OnboardingStore;
import org.springframework.stereotype.Service;

@Service
public class GetOnboardingStatusUseCase implements GetOnboardingStatusQuery {

    private final OnboardingStore store;

    public GetOnboardingStatusUseCase(OnboardingStore store) {
        this.store = store;
    }

    @Override
    public OnboardingRecord get(String tenantId, String subject) {
        return store.find(tenantId, subject).orElse(null);
    }
}
