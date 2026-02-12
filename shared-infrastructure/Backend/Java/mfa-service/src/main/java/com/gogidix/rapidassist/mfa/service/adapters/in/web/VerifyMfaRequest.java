package com.gogidix.rapidassist.mfa.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record VerifyMfaRequest(
        @NotBlank String subject,
        @NotBlank String enrollmentId,
        @NotBlank String code
) {
}
