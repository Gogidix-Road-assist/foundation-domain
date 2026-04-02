package com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.model.ConsentPreferences;

import java.util.Optional;

public interface ConsentStore {

    Optional<ConsentPreferences> find(String tenantId, String subject);

    ConsentPreferences upsert(ConsentPreferences preferences);
}
