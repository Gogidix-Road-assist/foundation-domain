package com.gogidix.rapidassist.user.profile.service.adapters.in.web;

import java.time.Instant;

public record UserProfileResponse(
        String subject,
        String displayName,
        String email,
        Instant updatedAt
) {
}
