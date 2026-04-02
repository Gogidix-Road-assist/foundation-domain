package com.gogidix.rapidassist.tenancy.configuration.service.adapters.in.web;

import com.gogidix.rapidassist.tenancy.configuration.service.domain.port.in.GetStatusQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    public String status() {
        return getStatusQuery.getStatus();
    }
}
