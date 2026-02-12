package com.gogidix.rapidassist.courier.adapter.service.adapters.in.web;

import com.gogidix.rapidassist.courier.adapter.service.domain.port.in.GetStatusQuery;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Status", description = "Service health and status APIs")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    @Operation(summary = "Get service status", description = "Returns the current status of the courier adapter service")
    public String status() {
        return getStatusQuery.getStatus();
    }

    /**
     * Helper method to extract tenantId from request context.
     * All tenant-scoped operations should use this method.
     *
     * @return the tenantId from the current request context
     * @throws IllegalStateException if tenantId is not present in the request context
     */
    protected String getTenantId() {
        return RequestContextHolder.get()
            .map(context -> context.tenantId())
            .orElseThrow(() -> new IllegalStateException("TenantId is required but not present in the request context"));
    }
}
