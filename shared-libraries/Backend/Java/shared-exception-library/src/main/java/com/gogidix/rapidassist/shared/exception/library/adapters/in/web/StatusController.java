package com.gogidix.rapidassist.shared.exception.library.adapters.in.web;

import com.gogidix.rapidassist.shared.exception.library.domain.port.in.GetStatusQuery;
import com.gogidix.rapidassist.shared.exception.library.exception.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    // Test endpoints for exception handler testing
    @GetMapping("/test/not-found")
    public void notFound() {
        throw new NotFoundException("TestEntity", "test-123");
    }

    @GetMapping("/test/bad-request")
    public void badRequest() {
        throw new BadRequestException("Invalid test data");
    }

    @PostMapping("/test/conflict")
    public void conflict(@RequestBody Map<String, String> request) {
        throw new ConflictException("TestEntity", "email", request.get("email"));
    }

    @GetMapping("/test/forbidden")
    public void forbidden() {
        throw new ForbiddenException("Access denied to this resource");
    }

    @GetMapping("/test/unauthorized")
    public void unauthorized() {
        throw new UnauthorizedException("Invalid authentication");
    }

    @GetMapping("/test/service-unavailable")
    public void serviceUnavailable() {
        throw new ServiceUnavailableException("External API", "Temporarily unavailable");
    }
}
