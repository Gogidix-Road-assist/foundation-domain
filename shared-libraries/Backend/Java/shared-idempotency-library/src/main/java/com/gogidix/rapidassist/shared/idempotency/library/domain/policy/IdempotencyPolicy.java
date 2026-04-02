package com.gogidix.rapidassist.shared.idempotency.library.domain.policy;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;

public interface IdempotencyPolicy {

    IdempotencyDecision decide(IdempotencyStore.IdempotencyRecord existing, String requestHash);
}
