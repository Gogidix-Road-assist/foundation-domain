package com.gogidix.rapidassist.pricing.service.domain.port.out;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;

import java.util.Optional;

public interface PriceBookStore {

    Optional<PriceBook> find(String tenantId);

    PriceBook upsert(PriceBook priceBook);
}
