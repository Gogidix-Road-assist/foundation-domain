package com.gogidix.rapidassist.shared.idempotency.library.infrastructure.noop;

import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;

import java.util.Optional;

public class NoOpIdempotencyStore implements IdempotencyStore {

    @Override
    public Optional<IdempotencyRecord> find(String key) {
        return Optional.empty();
    }

    @Override
    public IdempotencyRecord saveNew(IdempotencyRecord record) {
        return record;
    }

    @Override
    public IdempotencyRecord update(IdempotencyRecord record) {
        return record;
    }
}
