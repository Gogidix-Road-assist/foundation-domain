package com.gogidix.rapidassist.shared.idempotency.library.domain;

public record IdempotencyContext(
        String idempotencyKey
) {
}
