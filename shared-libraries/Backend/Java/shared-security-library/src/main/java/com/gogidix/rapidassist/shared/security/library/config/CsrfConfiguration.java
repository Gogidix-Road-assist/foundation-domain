package com.gogidix.rapidassist.shared.security.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import jakarta.servlet.http.HttpServletRequest;

/**
 * CSRF protection configuration.
 */
@Configuration
@EnableWebSecurity
public class CsrfConfiguration {

    /**
     * Default CSRF token repository using cookies
     * @return CSRF token repository
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        // Use cookie-based CSRF token repository for SPA compatibility
        return CookieCsrfTokenRepository.withHttpOnlyFalse();
    }

    /**
     * Configure CSRF settings
     * @param http HTTP security builder
     * @return security filter chain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers(this::isCsrfDisabledForPath)
            );

        return http.build();
    }

    /**
     * Determine if CSRF should be disabled for specific paths
     * @param request HTTP request
     * @return true if CSRF should be disabled
     */
    private Boolean isCsrfDisabledForPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // Disable CSRF for stateless API endpoints that use JWT
        if (path.startsWith("/api/auth/") || path.startsWith("/oauth/")) {
            return true;
        }

        // Disable CSRF for public endpoints
        if (path.startsWith("/public/") || path.startsWith("/health")) {
            return true;
        }

        // Disable CSRF for read-only operations (safe methods)
        if ("GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method) || "TRACE".equals(method)) {
            return true;
        }

        return false;
    }

    /**
     * Check if request should have CSRF protection
     * @param request HTTP request
     * @return true if CSRF protection is required
     */
    public Boolean requiresCsrfProtection(HttpServletRequest request) {
        return !isCsrfDisabledForPath(request);
    }

    /**
     * Get CSRF header name
     * @return CSRF header name
     */
    public String getCsrfHeaderName() {
        return "X-CSRF-TOKEN";
    }

    /**
     * Get CSRF parameter name
     * @return CSRF parameter name
     */
    public String getCsrfParameterName() {
        return "_csrf";
    }
}
