package com.gogidix.rapidassist.pricing.service.application.usecase;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;
import com.gogidix.rapidassist.pricing.service.domain.port.in.UpsertPriceBookCommand;
import com.gogidix.rapidassist.pricing.service.domain.port.out.PriceBookStore;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Service
public class UpsertPriceBookUseCase implements UpsertPriceBookCommand {

    private final PriceBookStore store;

    public UpsertPriceBookUseCase(PriceBookStore store) {
        this.store = store;
    }

    @Override
    public PriceBook upsert(String tenantId, String currency, Map<String, BigDecimal> prices) {
        return store.upsert(new PriceBook(tenantId, currency, prices, Instant.now()));
    }
}
