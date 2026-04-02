package com.gogidix.rapidassist.onboarding.service.application.usecase;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;
import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingStatus;
import com.gogidix.rapidassist.onboarding.service.domain.port.in.StartOnboardingCommand;
import com.gogidix.rapidassist.onboarding.service.domain.port.out.OnboardingStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class StartOnboardingUseCase implements StartOnboardingCommand {

    private final OnboardingStore store;

    public StartOnboardingUseCase(OnboardingStore store) {
        this.store = store;
    }

    @Override
    public OnboardingRecord start(String tenantId, String subject) {
        return store.upsert(new OnboardingRecord(tenantId, subject, OnboardingStatus.STARTED, Instant.now()));
    }
}
