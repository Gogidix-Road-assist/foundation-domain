package com.gogidix.rapidassist.currency.converter.service.domain.port.out;

import java.math.BigDecimal;

public interface ExchangeRateProvider {

    BigDecimal rate(String tenantId, String fromCurrency, String toCurrency);
}
