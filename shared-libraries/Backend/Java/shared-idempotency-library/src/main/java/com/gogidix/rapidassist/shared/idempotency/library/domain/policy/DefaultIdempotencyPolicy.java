package com.gogidix.rapidassist.shared.idempotency.library.domain.policy;

import com.gogidix.rapidassist.shared.idempotency.library.autoconfigure.IdempotencyProperties;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;

public class DefaultIdempotencyPolicy implements IdempotencyPolicy {

    private final IdempotencyProperties properties;

    public DefaultIdempotencyPolicy(IdempotencyProperties properties) {
        this.properties = properties;
    }

    @Override
    public IdempotencyDecision decide(IdempotencyStore.IdempotencyRecord existing, String requestHash) {
        if (existing == null) {
            return IdempotencyDecision.acceptNew();
        }

        if (existing.status() == IdempotencyStore.Status.IN_PROGRESS) {
            return IdempotencyDecision.inProgress(existing);
        }

        if (!properties.isEnforceRequestHash()) {
            return IdempotencyDecision.replay(existing);
        }

        String recordedHash = existing.requestHash();
        if (recordedHash == null || recordedHash.isBlank()) {
            return IdempotencyDecision.replay(existing);
        }

        if (recordedHash.equals(requestHash)) {
            return IdempotencyDecision.replay(existing);
        }

        return IdempotencyDecision.conflict(existing);
    }
}
