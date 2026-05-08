package com.gogidix.rapidassist.session.token.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record IntrospectSessionTokenRequest(
        @NotBlank String token
) {
}
