package com.gogidix.rapidassist.identity.service.adapters.in.web;

import com.gogidix.rapidassist.identity.service.domain.port.in.GetStatusQuery;
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
