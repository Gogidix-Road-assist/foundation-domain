package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out.ConsentStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryConsentStore implements ConsentStore {

    private final ConcurrentHashMap<String, ConsentPreferences> byTenantAndSubject = new ConcurrentHashMap<>();

    private String key(String tenantId, String subject) {
        return tenantId + "::" + subject;
    }

    @Override
    public Optional<ConsentPreferences> find(String tenantId, String subject) {
        return Optional.ofNullable(byTenantAndSubject.get(key(tenantId, subject)));
    }

    @Override
    public ConsentPreferences upsert(ConsentPreferences preferences) {
        byTenantAndSubject.put(key(preferences.tenantId(), preferences.subject()), preferences);
        return preferences;
    }
}
