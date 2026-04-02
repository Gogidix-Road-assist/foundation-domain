package com.gogidix.rapidassist.onboarding.service.domain.model;

import java.time.Instant;

public record OnboardingRecord(
        String tenantId,
        String subject,
        OnboardingStatus status,
        Instant updatedAt
) {
}
