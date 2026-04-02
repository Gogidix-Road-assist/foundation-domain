package com.gogidix.rapidassist.mfa.service.adapters.in.web;

import java.time.Instant;

public record EnrollMfaResponse(
        String enrollmentId,
        Instant enrolledAt
) {
}
