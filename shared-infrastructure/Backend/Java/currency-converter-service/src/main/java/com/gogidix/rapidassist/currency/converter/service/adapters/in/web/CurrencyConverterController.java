package com.gogidix.rapidassist.currency.converter.service.adapters.in.web;

import com.gogidix.rapidassist.currency.converter.service.domain.model.ConversionResult;
import com.gogidix.rapidassist.currency.converter.service.domain.port.in.ConvertCurrencyQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/currency")
@Tag(name = "Currency Converter", description = "Currency conversion and exchange rate APIs")
@SecurityRequirement(name = "bearerAuth")
public class CurrencyConverterController {

    private final ConvertCurrencyQuery convertCurrencyQuery;

    public CurrencyConverterController(ConvertCurrencyQuery convertCurrencyQuery) {
        this.convertCurrencyQuery = convertCurrencyQuery;
    }

    @PostMapping("/convert")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Convert currency", description = "Converts an amount from one currency to another using current exchange rates")
    public ConversionResponse convert(@Valid @RequestBody ConvertCurrencyRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        ConversionResult result = convertCurrencyQuery.convert(tenantId, request.amount(), request.fromCurrency(), request.toCurrency());
        return new ConversionResponse(result.fromCurrency(), result.toCurrency(), result.amount(), result.convertedAmount(), result.rate());
    }
}
