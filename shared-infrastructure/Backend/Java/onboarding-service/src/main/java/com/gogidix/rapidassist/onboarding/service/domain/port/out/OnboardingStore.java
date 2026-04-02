package com.gogidix.rapidassist.onboarding.service.domain.port.out;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;

import java.util.Optional;

public interface OnboardingStore {

    Optional<OnboardingRecord> find(String tenantId, String subject);

    OnboardingRecord upsert(OnboardingRecord record);
}
