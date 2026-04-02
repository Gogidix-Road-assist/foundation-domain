package com.gogidix.rapidassist.identity.service.adapters.in.web;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/identity")
public class WhoAmIController {

    @GetMapping("/whoami")
    public WhoAmIResponse whoAmI() {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        String country = RequestContextHolder.get().map(c -> c.country()).orElse(null);
        String correlationId = RequestContextHolder.get().map(c -> c.correlationId()).orElse(null);

        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        return new WhoAmIResponse(tenantId, country, correlationId);
    }
}
