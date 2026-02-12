package com.gogidix.rapidassist.idempotency.service.application.usecase;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyStatus;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.ReserveIdempotencyKeyCommand;
import com.gogidix.rapidassist.idempotency.service.domain.port.out.IdempotencyStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReserveIdempotencyKeyUseCase implements ReserveIdempotencyKeyCommand {

    private final IdempotencyStore store;

    public ReserveIdempotencyKeyUseCase(IdempotencyStore store) {
        this.store = store;
    }

    @Override
    public IdempotencyRecord reserve(String tenantId, String key) {
        return store.find(tenantId, key).orElseGet(() -> store.save(new IdempotencyRecord(tenantId, key, IdempotencyStatus.RESERVED, Instant.now())));
    }
}
