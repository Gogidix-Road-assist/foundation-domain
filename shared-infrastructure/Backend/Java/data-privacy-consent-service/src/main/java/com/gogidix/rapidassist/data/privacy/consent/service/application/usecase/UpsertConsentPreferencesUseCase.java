package com.gogidix.rapidassist.data.privacy.consent.service.application.usecase;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in.UpsertConsentPreferencesCommand;
import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out.ConsentStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UpsertConsentPreferencesUseCase implements UpsertConsentPreferencesCommand {

    private final ConsentStore store;

    public UpsertConsentPreferencesUseCase(ConsentStore store) {
        this.store = store;
    }

    @Override
    public ConsentPreferences upsert(String tenantId, String subject, boolean termsAccepted, boolean marketingEmails) {
        return store.upsert(new ConsentPreferences(tenantId, subject, termsAccepted, marketingEmails, Instant.now()));
    }
}
