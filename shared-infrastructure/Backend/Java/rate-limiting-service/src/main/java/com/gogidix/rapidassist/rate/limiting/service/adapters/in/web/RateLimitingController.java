package com.gogidix.rapidassist.rate.limiting.service.adapters.in.web;

import com.gogidix.rapidassist.rate.limiting.service.domain.model.RateLimitDecision;
import com.gogidix.rapidassist.rate.limiting.service.domain.port.in.CheckRateLimitCommand;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/rate-limits")
public class RateLimitingController {

    private final CheckRateLimitCommand checkRateLimitCommand;

    public RateLimitingController(CheckRateLimitCommand checkRateLimitCommand) {
        this.checkRateLimitCommand = checkRateLimitCommand;
    }

    @PostMapping("/check")
    @ResponseStatus(HttpStatus.OK)
    public RateLimitCheckResponse check(@Valid @RequestBody RateLimitCheckRequest request) {
        String tenantId = RequestContextHolder.get().map(c -> c.tenantId()).orElse(null);
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenantId in JWT context");
        }

        RateLimitDecision decision = checkRateLimitCommand.check(
                tenantId,
                request.key(),
                request.limit(),
                Duration.ofSeconds(request.windowSeconds())
        );

        return new RateLimitCheckResponse(
                decision.allowed(),
                decision.limit(),
                decision.currentCount(),
                decision.remaining(),
                decision.resetAt()
        );
    }
}
