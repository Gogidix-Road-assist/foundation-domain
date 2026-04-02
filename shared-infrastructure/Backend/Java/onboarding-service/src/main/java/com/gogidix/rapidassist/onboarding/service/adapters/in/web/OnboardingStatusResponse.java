package com.gogidix.rapidassist.onboarding.service.adapters.in.web;

import com.gogidix.rapidassist.onboarding.service.domain.model.OnboardingStatus;

import java.time.Instant;

public record OnboardingStatusResponse(
        String subject,
        OnboardingStatus status,
        Instant updatedAt
) {
}
