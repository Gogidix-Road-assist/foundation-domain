package com.gogidix.rapidassist.idempotency.service.adapters.in.web;

import jakarta.validation.constraints.NotBlank;

public record ReserveIdempotencyRequest(
        @NotBlank String key
) {
}
