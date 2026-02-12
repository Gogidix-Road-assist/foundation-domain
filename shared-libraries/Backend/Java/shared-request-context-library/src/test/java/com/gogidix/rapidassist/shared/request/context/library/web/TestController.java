package com.gogidix.rapidassist.shared.request.context.library.web;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Test controller for RequestContextFilter testing.
 */
@RestController
public class TestController {

    @GetMapping("/api/test/context")
    public Map<String, String> getContext(
            @RequestHeader(value = "X-Tenant-Id", required = false) String tenantId,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-Request-Id", required = false) String requestId) {

        Map<String, String> response = new HashMap<>();

        RequestContextHolder.get().ifPresentOrElse(
                context -> {
                    response.put("correlationId", context.correlationId());
                    response.put("tenantId", context.tenantId());
                    response.put("userId", context.userId());
                    response.put("requestId", context.requestId());
                    response.put("country", context.country());
                    response.put("status", "context-found");
                },
                () -> response.put("status", "no-context")
        );

        return response;
    }

    @GetMapping("/api/test/async")
    public Map<String, String> testAsync() {
        Map<String, String> response = new HashMap<>();

        RequestContextHolder.get().ifPresentOrElse(
                context -> {
                    response.put("tenantId", context.tenantId());
                    response.put("userId", context.userId());
                },
                () -> response.put("status", "no-context")
        );

        return response;
    }
}
