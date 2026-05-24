package com.gogidix.rapidassist.user.profile.service.adapters.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpsertUserProfileRequest(
        @NotBlank String displayName,
        @Email String email
) {
}
