package com.gogidix.rapidassist.shared.idempotency.library.domain.policy;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;

import java.util.Optional;

public record IdempotencyDecision(
        Type type,
        Optional<IdempotencyStore.IdempotencyRecord> existing
) {

    public static IdempotencyDecision acceptNew() {
        return new IdempotencyDecision(Type.ACCEPT_NEW, Optional.empty());
    }

    public static IdempotencyDecision replay(IdempotencyStore.IdempotencyRecord record) {
        return new IdempotencyDecision(Type.REPLAY, Optional.of(record));
    }

    public static IdempotencyDecision conflict(IdempotencyStore.IdempotencyRecord record) {
        return new IdempotencyDecision(Type.CONFLICT, Optional.of(record));
    }

    public static IdempotencyDecision inProgress(IdempotencyStore.IdempotencyRecord record) {
        return new IdempotencyDecision(Type.IN_PROGRESS, Optional.of(record));
    }

    public enum Type {
        ACCEPT_NEW,
        REPLAY,
        CONFLICT,
        IN_PROGRESS
    }
}
