package com.gogidix.rapidassist.onboarding.service.adapters.in.web;

import com.gogidix.rapidassist.onboarding.service.domain.port.in.GetStatusQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    public String status() {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }
        return getStatusQuery.getStatus();
    }
}
