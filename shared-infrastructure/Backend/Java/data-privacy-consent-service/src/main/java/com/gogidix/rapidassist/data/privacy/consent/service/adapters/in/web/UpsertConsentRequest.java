package com.gogidix.rapidassist.data.privacy.consent.service.adapters.in.web;

import jakarta.validation.constraints.NotNull;

public record UpsertConsentRequest(
        @NotNull Boolean termsAccepted,
        @NotNull Boolean marketingEmails
) {
}
