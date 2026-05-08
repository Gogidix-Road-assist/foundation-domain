package com.gogidix.rapidassist.idempotency.service.domain.port.out;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;

import java.util.Optional;

public interface IdempotencyStore {

    Optional<IdempotencyRecord> find(String tenantId, String key);

    IdempotencyRecord save(IdempotencyRecord record);
}
