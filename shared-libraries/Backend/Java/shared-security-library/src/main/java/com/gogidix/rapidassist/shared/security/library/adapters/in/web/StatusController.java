package com.gogidix.rapidassist.shared.security.library.adapters.in.web;

import com.gogidix.rapidassist.shared.security.library.domain.port.in.GetStatusQuery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("securityStatusController")
public class StatusController {

    private final GetStatusQuery getStatusQuery;

    public StatusController(@Qualifier("securityGetStatusUseCase") GetStatusQuery getStatusQuery) {
        this.getStatusQuery = getStatusQuery;
    }

    @GetMapping("/status")
    public String status() {
        return getStatusQuery.getStatus();
    }
}
