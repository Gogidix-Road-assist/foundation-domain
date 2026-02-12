package com.gogidix.rapidassist.pricing.service.domain.port.in;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;

import java.math.BigDecimal;
import java.util.Map;

public interface UpsertPriceBookCommand {

    PriceBook upsert(String tenantId, String currency, Map<String, BigDecimal> prices);
}
