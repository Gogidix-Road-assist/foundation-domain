package com.gogidix.rapidassist.data.privacy.consent.service.application.usecase;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.GetConsentPreferencesQuery;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out.ConsentStore;
import org.springframework.stereotype.Service;

@Service
public class GetConsentPreferencesUseCase implements GetConsentPreferencesQuery {

    private final ConsentStore store;

    public GetConsentPreferencesUseCase(ConsentStore store) {
        this.store = store;
    }

    @Override
    public ConsentPreferences get(String tenantId, String subject) {
        return store.find(tenantId, subject).orElse(null);
    }
}
