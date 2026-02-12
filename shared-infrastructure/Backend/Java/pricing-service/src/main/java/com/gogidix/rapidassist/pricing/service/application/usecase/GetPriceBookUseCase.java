package com.gogidix.rapidassist.pricing.service.application.usecase;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;
import com.gogidix.rapidassist.pricing.service.domain.port.in.GetPriceBookQuery;
import com.gogidix.rapidassist.pricing.service.domain.port.out.PriceBookStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class GetPriceBookUseCase implements GetPriceBookQuery {

    private final PriceBookStore store;

    public GetPriceBookUseCase(PriceBookStore store) {
        this.store = store;
    }

    @Override
    public PriceBook get(String tenantId) {
        return store.find(tenantId)
                .orElseGet(() -> store.upsert(new PriceBook(tenantId, "USD", Map.of("DEFAULT", BigDecimal.ZERO), Instant.now())));
    }
}
