package com.gogidix.rapidassist.onboarding.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingRecord;
import com.gogidix.rapidassist.onboarding.service.domain.port.out.OnboardingStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryOnboardingStore implements OnboardingStore {

    private final ConcurrentHashMap<String, OnboardingRecord> byTenantAndSubject = new ConcurrentHashMap<>();

    private String key(String tenantId, String subject) {
        return tenantId + "::" + subject;
    }

    @Override
    public Optional<OnboardingRecord> find(String tenantId, String subject) {
        return Optional.ofNullable(byTenantAndSubject.get(key(tenantId, subject)));
    }

    @Override
    public OnboardingRecord upsert(OnboardingRecord record) {
        byTenantAndSubject.put(key(record.tenantId(), record.subject()), record);
        return record;
    }
}
