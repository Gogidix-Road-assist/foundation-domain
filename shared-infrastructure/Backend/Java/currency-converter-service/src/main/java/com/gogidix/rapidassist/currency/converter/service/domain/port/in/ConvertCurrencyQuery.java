package com.gogidix.rapidassist.currency.converter.service.domain.port.in;

import com.gogidix.rapidassist.currency.converter.service.domain.model.ConversionResult;

import java.math.BigDecimal;

public interface ConvertCurrencyQuery {

    ConversionResult convert(String tenantId, BigDecimal amount, String fromCurrency, String toCurrency);
}
