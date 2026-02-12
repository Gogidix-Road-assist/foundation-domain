package com.gogidix.rapidassist.currency.converter.service.infrastructure.rates.memory;

import com.gogidix.rapidassist.currency.converter.service.domain.port.out.ExchangeRateProvider;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryExchangeRateProvider implements ExchangeRateProvider {

    private final ConcurrentHashMap<String, BigDecimal> rates = new ConcurrentHashMap<>();

    public InMemoryExchangeRateProvider() {
        rates.put(key("USD", "USD"), BigDecimal.ONE);
        rates.put(key("EUR", "EUR"), BigDecimal.ONE);
        rates.put(key("USD", "EUR"), new BigDecimal("0.90"));
        rates.put(key("EUR", "USD"), new BigDecimal("1.11"));
    }

    private String key(String from, String to) {
        return from.toUpperCase() + "::" + to.toUpperCase();
    }

    @Override
    public BigDecimal rate(String tenantId, String fromCurrency, String toCurrency) {
        return rates.getOrDefault(key(fromCurrency, toCurrency), BigDecimal.ONE);
    }
}
