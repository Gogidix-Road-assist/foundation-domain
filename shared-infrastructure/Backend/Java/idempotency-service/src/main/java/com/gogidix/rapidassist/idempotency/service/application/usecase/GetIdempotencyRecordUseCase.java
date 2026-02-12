package com.gogidix.rapidassist.idempotency.service.application.usecase;

import com.gogidix.rapidassist.idempotency.service.domain.model.IdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.domain.port.in.GetIdempotencyRecordQuery;
import com.gogidix.rapidassist.idempotency.service.domain.port.out.IdempotencyStore;
import org.springframework.stereotype.Service;

@Service
public class GetIdempotencyRecordUseCase implements GetIdempotencyRecordQuery {

    private final IdempotencyStore store;

    public GetIdempotencyRecordUseCase(IdempotencyStore store) {
        this.store = store;
    }

    @Override
    public IdempotencyRecord get(String tenantId, String key) {
        return store.find(tenantId, key).orElse(null);
    }
}
