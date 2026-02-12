package com.gogidix.rapidassist.pricing.service.adapters.in.web;

import com.gogidix.rapidassist.pricing.service.domain.model.PriceBook;
import com.gogidix.rapidassist.pricing.service.domain.port.in.GetPriceBookQuery;
import com.gogidix.rapidassist.pricing.service.domain.port.in.UpsertPriceBookCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/pricing")
public class PricingController {

    private final GetPriceBookQuery getPriceBookQuery;
    private final UpsertPriceBookCommand upsertPriceBookCommand;

    public PricingController(GetPriceBookQuery getPriceBookQuery, UpsertPriceBookCommand upsertPriceBookCommand) {
        this.getPriceBookQuery = getPriceBookQuery;
        this.upsertPriceBookCommand = upsertPriceBookCommand;
    }

    @GetMapping("/price-book")
    @ResponseStatus(HttpStatus.OK)
    public PriceBookResponse get() {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        PriceBook book = getPriceBookQuery.get(tenantId);
        return new PriceBookResponse(book.currency(), book.prices(), book.updatedAt());
    }

    @PostMapping("/price-book")
    @ResponseStatus(HttpStatus.OK)
    public PriceBookResponse upsert(@Valid @RequestBody UpsertPriceBookRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        PriceBook book = upsertPriceBookCommand.upsert(tenantId, request.currency(), request.prices());
        return new PriceBookResponse(book.currency(), book.prices(), book.updatedAt());
    }
}
