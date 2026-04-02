package com.gogidix.rapidassist.api.keys.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record IssueApiKeyRequest(
        @NotBlank String subject
) {
}
