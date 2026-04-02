package com.gogidix.rapidassist.shared.idempotency.library.autoconfigure;

import com.gogidix.rapidassist.shared.idempotency.library.domain.IdempotencyContext;
import com.gogidix.rapidassist.shared.idempotency.library.domain.IdempotencyContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class IdempotencyKeyFilter extends OncePerRequestFilter {

    private final IdempotencyProperties properties;

    public IdempotencyKeyFilter(IdempotencyProperties properties) {
        this.properties = properties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String headerName = properties.getKeyHeader();
        String key = headerName == null || headerName.isBlank() ? null : request.getHeader(headerName);

        if (properties.isRequired() && (key == null || key.isBlank())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required header: " + headerName);
            return;
        }

        if (key != null && !key.isBlank()) {
            IdempotencyContextHolder.set(new IdempotencyContext(key));
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            IdempotencyContextHolder.clear();
        }
    }
}
