package com.gogidix.rapidassist.idempotency.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.port.out.IdempotencyStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIdempotencyStore implements IdempotencyStore {

    private final ConcurrentHashMap<String, IdempotencyRecord> byTenantAndKey = new ConcurrentHashMap<>();

    private String key(String tenantId, String key) {
        return tenantId + "::" + key;
    }

    @Override
    public Optional<IdempotencyRecord> find(String tenantId, String key) {
        return Optional.ofNullable(byTenantAndKey.get(key(tenantId, key)));
    }

    @Override
    public IdempotencyRecord save(IdempotencyRecord record) {
        byTenantAndKey.put(key(record.tenantId(), record.key()), record);
        return record;
    }
}
