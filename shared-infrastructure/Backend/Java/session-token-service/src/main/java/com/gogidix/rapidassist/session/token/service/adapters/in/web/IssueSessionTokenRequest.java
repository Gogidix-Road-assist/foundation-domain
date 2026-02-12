package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record IssueSessionTokenRequest(
        @NotBlank String subject,
        @Positive long ttlSeconds
) {
}
