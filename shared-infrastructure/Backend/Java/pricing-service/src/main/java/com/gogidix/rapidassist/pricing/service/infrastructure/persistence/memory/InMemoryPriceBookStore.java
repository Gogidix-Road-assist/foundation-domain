package com.gogidix.rapidassist.pricing.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;
import com.gogidix.rapidassist.pricing.service.domain.port.out.PriceBookStore;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryPriceBookStore implements PriceBookStore {

    private final ConcurrentHashMap<String, PriceBook> byTenantId = new ConcurrentHashMap<>();

    @Override
    public Optional<PriceBook> find(String tenantId) {
        return Optional.ofNullable(byTenantId.get(tenantId));
    }

    @Override
    public PriceBook upsert(PriceBook priceBook) {
        byTenantId.put(priceBook.tenantId(), priceBook);
        return priceBook;
    }
}
