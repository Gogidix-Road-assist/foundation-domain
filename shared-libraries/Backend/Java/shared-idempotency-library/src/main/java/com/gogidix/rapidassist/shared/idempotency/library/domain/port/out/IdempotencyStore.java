package com.gogidix.rapidassist.shared.idempotency.library.domain.port.out;

import java.time.Instant;
import java.util.Optional;

public interface IdempotencyStore {

    Optional<IdempotencyRecord> find(String key);

    IdempotencyRecord saveNew(IdempotencyRecord record);

    IdempotencyRecord update(IdempotencyRecord record);

    record IdempotencyRecord(
            String key,
            Status status,
            Instant createdAt,
            Instant updatedAt,
            String requestHash,
            Integer responseStatus,
            String responseHash
    ) {
    }

    enum Status {
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}
