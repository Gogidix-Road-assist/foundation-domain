package com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;

public interface UpsertConsentPreferencesCommand {

    ConsentPreferences upsert(String tenantId, String subject, boolean termsAccepted, boolean marketingEmails);
}
