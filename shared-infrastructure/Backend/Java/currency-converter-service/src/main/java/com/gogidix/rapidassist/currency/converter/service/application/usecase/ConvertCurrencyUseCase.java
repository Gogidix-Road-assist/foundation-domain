package com.gogidix.rapidassist.currency.converter.service.application.usecase;

import com.gogidix.rapidassist.currency.converter.service.domain.model.ConversionResult;
import com.gogidix.rapidassist.currency.converter.service.domain.port.in.ConvertCurrencyQuery;
import com.gogidix.rapidassist.currency.converter.service.domain.port.out.ExchangeRateProvider;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ConvertCurrencyUseCase implements ConvertCurrencyQuery {

    private final ExchangeRateProvider exchangeRateProvider;

    public ConvertCurrencyUseCase(ExchangeRateProvider exchangeRateProvider) {
        this.exchangeRateProvider = exchangeRateProvider;
    }

    @Override
    public ConversionResult convert(String tenantId, BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal rate = exchangeRateProvider.rate(tenantId, fromCurrency, toCurrency);
        BigDecimal converted = amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return new ConversionResult(fromCurrency, toCurrency, amount, converted, rate);
    }
}
