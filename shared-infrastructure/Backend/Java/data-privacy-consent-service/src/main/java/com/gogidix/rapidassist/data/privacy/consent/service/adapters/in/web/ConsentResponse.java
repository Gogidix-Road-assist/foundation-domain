package com.gogidix.rapidassist.data.privacy.consent.service.adapters.in.web;

import java.time.Instant;

public record ConsentResponse(
        String subject,
        boolean termsAccepted,
        boolean marketingEmails,
        Instant updatedAt
) {
}
