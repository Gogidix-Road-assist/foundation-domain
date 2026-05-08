package com.gogidix.rapidassist.mfa.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record EnrollMfaRequest(
        @NotBlank String subject,
        @NotBlank String method
) {
}
