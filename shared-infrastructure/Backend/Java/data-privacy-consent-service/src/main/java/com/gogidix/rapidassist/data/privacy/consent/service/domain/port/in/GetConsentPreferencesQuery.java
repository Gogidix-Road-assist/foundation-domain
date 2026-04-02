package com.gogidix.rapidassist.data.privacy.consent.service.domain.port.in;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;

public interface GetConsentPreferencesQuery {

    ConsentPreferences get(String tenantId, String subject);
}
