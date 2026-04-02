package com.gogidix.rapidassist.data.privacy.consent.service.domain.model;

import java.time.Instant;

public record ConsentPreferences(
        String tenantId,
        String subject,
        boolean termsAccepted,
        boolean marketingEmails,
        Instant updatedAt
) {
}
