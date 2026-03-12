package com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.config;

import com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.context.MongoTenantContext;
import com.mongodb.client.MongoClient;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.io.IOException;
import java.util.Optional;

/**
 * Auto-configuration for MongoDB multi-tenancy support.
 * <p>
 * This configuration class sets up the necessary components for tenant-aware
 * MongoDB operations in multi-tenant SaaS applications. It configures:
 * </p>
 * <ul>
 *   <li>Tenant request filter - Extracts tenant ID from HTTP requests</li>
 *   <li>MongoDB auditing - Tracks created/updated timestamps and users</li>
 *   <li>Tenant-aware document listener - Auto-populates tenant_id before save</li>
 *   <li>MongoDB repositories - Enables Spring Data MongoDB repositories</li>
 * </ul>
 * <p>
 * <strong>Tenant Identification:</strong> The filter extracts tenant ID from:
 * </p>
 * <ol>
 *   <li>HTTP Header: {@code X-Tenant-Id} (preferred)</li>
 *   <li>Query Parameter: {@code tenantId} (fallback)</li>
 *   <li>Default: {@code 1L} if neither is provided</li>
 * </ol>
 * <p>
 * <strong>Activation:</strong> This configuration is automatically activated when:
 * <ul>
 *   <li>MongoDB client is on the classpath</li>
 *   <li>{@code mongodb.multitenancy.enabled} is true (default: true)</li>
 * </ul>
 * </p>
 * <p>
 * Usage in application.yml:
 * <pre>{@code
 * spring:
 *   data:
 *     mongodb:
 *       uri: mongodb://localhost:27017/rapidassist
 * mongodb:
 *   multitenancy:
 *     enabled: true
 *     default-tenant-id: 1
 *     header-name: X-Tenant-Id
 * }</pre>
 * </p>
 *
 * @see MongoTenantContext
 * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument
 */
@Configuration
@ConditionalOnClass(MongoClient.class)
@ConditionalOnProperty(
    prefix = "mongodb.multitenancy",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
@EnableMongoAuditing(auditorAwareRef = "mongoAuditorProvider", modifyOnCreate = true)
public class MongoTenantConfiguration {

    private static final Logger log = LoggerFactory.getLogger(MongoTenantConfiguration.class);

    /**
     * Configures the tenant request filter as a Spring Bean.
     * <p>
     * This filter runs before each request to extract the tenant ID from
     * HTTP headers or query parameters and set it in the MongoTenantContext.
     * </p>
     *
     * @return FilterRegistrationBean for the tenant filter
     */
    @Bean
    @ConditionalOnMissingBean(name = "mongoTenantFilter")
    public FilterRegistrationBean<Filter> mongoTenantFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new TenantRequestFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // Run first, before Spring Security
        registration.setName("MongoTenantFilter");
        log.debug("Registered MongoTenantFilter for multi-tenancy support");
        return registration;
    }

    /**
     * Provides the auditor for MongoDB auditing operations.
     * <p>
     * The auditor tracks which user created or last modified a document.
     * By default, returns "system" for non-user-initiated operations.
     * </p>
     * <p>
     * Override this bean to provide actual user tracking from your
     * authentication context (e.g., Spring Security).
     * </p>
     *
     * @return AuditorAware for user tracking
     */
    @Bean
    @ConditionalOnMissingBean(name = "mongoAuditorProvider")
    public AuditorAware<String> mongoAuditorProvider() {
        return () -> {
            // Try to get user from security context if available (using reflection to avoid hard dependency)
            try {
                // Use reflection to avoid hard dependency on Spring Security
                Class<?> securityContextHolderClass =
                    Class.forName("org.springframework.security.core.context.SecurityContextHolder");
                Object context = securityContextHolderClass.getMethod("getContext").invoke(null);

                if (context != null) {
                    Object authentication = context.getClass()
                        .getMethod("getAuthentication")
                        .invoke(context);

                    if (authentication != null) {
                        Object principal = authentication.getClass()
                            .getMethod("getPrincipal")
                            .invoke(authentication);

                        if (principal instanceof String) {
                            return Optional.of((String) principal);
                        }

                        // Try to get username from UserDetails
                        try {
                            String username = (String) principal.getClass()
                                .getMethod("getUsername")
                                .invoke(principal);
                            return Optional.of(username);
                        } catch (Exception e) {
                            // Not a UserDetails object, use toString
                            return Optional.of(principal.toString());
                        }
                    }
                }
            } catch (ClassNotFoundException e) {
                log.trace("Spring Security not available for MongoDB auditing");
            } catch (Exception e) {
                log.trace("Security context not available for MongoDB auditing: {}", e.getMessage());
            }
            return Optional.of("system");
        };
    }

    /**
     * Configures the document event listener for auto-populating tenant_id.
     * <p>
     * This listener runs before each document save to ensure tenant_id is
     * populated from the MongoTenantContext if not already set.
     * </p>
     *
     * @return TenantAwareDocumentListener for auto-populating tenant_id
     */
    @Bean
    @ConditionalOnMissingBean(TenantAwareDocumentListener.class)
    public TenantAwareDocumentListener tenantAwareDocumentListener() {
        return new TenantAwareDocumentListener();
    }

    /**
     * Servlet filter that extracts and sets the tenant ID from HTTP requests.
     * <p>
     * This filter examines each incoming request for tenant identification and
     * stores it in the MongoTenantContext for the duration of the request.
     * The context is automatically cleared after request processing.
     * </p>
     * <p>
     * <strong>Tenant Resolution Order:</strong>
     * </p>
     * <ol>
     *   <li>HTTP Header: {@code X-Tenant-Id}</li>
     *   <li>Query Parameter: {@code tenantId}</li>
     *   <li>Default value: {@code 1L}</li>
     * </ol>
     *
     * @see MongoTenantContext
     */
    public static class TenantRequestFilter implements Filter {

        private static final Logger filterLog = LoggerFactory.getLogger(TenantRequestFilter.class);
        private static final String DEFAULT_HEADER = "X-Tenant-Id";
        private static final String DEFAULT_PARAM = "tenantId";
        private static final Long DEFAULT_TENANT = 1L;

        private String headerName = DEFAULT_HEADER;
        private String paramName = DEFAULT_PARAM;
        private Long defaultTenantId = DEFAULT_TENANT;

        /**
         * Filters the request to extract and set tenant ID.
         * <p>
         * Tenant ID is extracted from headers or query parameters and stored
         * in MongoTenantContext for the duration of the request.
         * </p>
         *
         * @param request  the servlet request
         * @param response the servlet response
         * @param chain    the filter chain
         * @throws IOException      if an I/O error occurs
         * @throws ServletException if a servlet error occurs
         */
        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {

            HttpServletRequest httpRequest = (HttpServletRequest) request;

            // Try to get tenant ID from header first
            String tenantId = httpRequest.getHeader(headerName);

            // Fall back to query parameter
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = httpRequest.getParameter(paramName);
            }

            // Parse and set tenant ID
            Long parsedTenantId = parseTenantId(tenantId);
            MongoTenantContext.setTenantId(parsedTenantId);

            try {
                if (filterLog.isTraceEnabled()) {
                    filterLog.trace("Set tenant context: {} for request: {} {}",
                        parsedTenantId, httpRequest.getMethod(), httpRequest.getRequestURI());
                }
                chain.doFilter(request, response);
            } finally {
                // Always clear the context to prevent thread pool contamination
                MongoTenantContext.clear();
                if (filterLog.isTraceEnabled()) {
                    filterLog.trace("Cleared tenant context for request: {} {}",
                        httpRequest.getMethod(), httpRequest.getRequestURI());
                }
            }
        }

        /**
         * Parses the tenant ID string to a Long value.
         * <p>
         * Handles parsing errors and returns the default tenant ID if parsing fails.
         * </p>
         *
         * @param tenantId the tenant ID string to parse
         * @return the parsed tenant ID, or default if invalid
         */
        private Long parseTenantId(String tenantId) {
            if (tenantId != null && !tenantId.isBlank()) {
                try {
                    return Long.parseLong(tenantId);
                } catch (NumberFormatException e) {
                    filterLog.warn("Invalid tenant ID format: '{}', using default: {}",
                        tenantId, defaultTenantId);
                }
            }
            return defaultTenantId;
        }

        /**
         * Sets a custom header name for tenant ID extraction.
         *
         * @param headerName the custom header name
         */
        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }

        /**
         * Sets a custom parameter name for tenant ID extraction.
         *
         * @param paramName the custom parameter name
         */
        public void setParamName(String paramName) {
            this.paramName = paramName;
        }

        /**
         * Sets a custom default tenant ID.
         *
         * @param defaultTenantId the default tenant ID
         */
        public void setDefaultTenantId(Long defaultTenantId) {
            this.defaultTenantId = defaultTenantId;
        }
    }

    /**
     * MongoDB document event listener for auto-populating tenant_id.
     * <p>
     * This listener intercepts document save operations and automatically
     * populates the tenant_id field from the MongoTenantContext if not already set.
     * </p>
     * <p>
     * This ensures that all documents are associated with the current tenant
     * without requiring manual tenant_id assignment in service layer code.
     * </p>
     *
     * @see MongoTenantContext
     * @see com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument
     */
    public static class TenantAwareDocumentListener extends AbstractMongoEventListener<Object> {

        private static final Logger listenerLog = LoggerFactory.getLogger(TenantAwareDocumentListener.class);

        /**
         * Handles BeforeConvertEvent to auto-populate tenant_id.
         * <p>
         * If the document is a TenantAwareDocument and tenant_id is not set,
         * it will be automatically populated from MongoTenantContext.
         * </p>
         *
         * @param event the before convert event
         */
        @Override
        public void onBeforeConvert(BeforeConvertEvent<Object> event) {
            Object source = event.getSource();

            if (source instanceof com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument doc) {
                Long currentTenantId = MongoTenantContext.getTenantId();

                // Only set if not already set (allows manual override)
                if (doc.getTenantId() == null) {
                    doc.setTenantId(currentTenantId);
                    if (listenerLog.isTraceEnabled()) {
                        listenerLog.trace("Auto-populated tenant_id: {} for document: {}",
                            currentTenantId, doc.getClass().getSimpleName());
                    }
                } else {
                    // Validate that the document belongs to the current tenant
                    if (!doc.getTenantId().equals(currentTenantId)) {
                        listenerLog.warn("Document tenant_id: {} does not match current tenant: {} for document: {}",
                            doc.getTenantId(), currentTenantId, doc.getClass().getSimpleName());
                    }
                }
            }
        }
    }
}
