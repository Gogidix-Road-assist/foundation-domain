/**
 * Shared request context library for multi-tenant Spring Boot applications.
 *
 * <p>This library provides a comprehensive solution for managing request-scoped
 * context information across your application, with particular focus on:</p>
 * <ul>
 *   <li>Multi-tenancy support - automatic tenant ID resolution from headers or JWT</li>
 *   <li>Request tracing - correlation ID propagation across service boundaries</li>
 *   <li>Context propagation - thread-safe context management across async boundaries</li>
 *   <li>User context - user ID tracking for audit and authorization</li>
 * </ul>
 *
 * <p>Getting started:</p>
 * <pre>{@code
 * // Add dependency to your pom.xml or build.gradle
 * // The library auto-configures itself in Spring Boot applications
 *
 * // Access context in your code
 * String tenantId = TenantContext.getTenantIdOrThrow();
 * String correlationId = RequestContextHolder.get()
 *     .map(RequestContext::correlationId)
 *     .orElse("unknown");
 * }</pre>
 *
 * <p>Configuration (application.yml):</p>
 * <pre>{@code
 * gogidix:
 *   request-context:
 *     tenant-id-header: X-Tenant-Id
 *     country-header: X-Country
 *     user-id-header: X-User-Id
 *     require-tenant-id: true
 *     prefer-jwt-claims: true
 *     async:
 *       enabled: true
 * }</pre>
 *
 * <p>Package structure:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.annotation} -
 *       Custom annotations for tenant awareness</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain} -
 *       Core domain models and context holders</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.exception} -
 *       Tenant-related exceptions</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.constant} -
 *       Configuration constants</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.async} -
 *       Async task context propagation</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.autoconfigure} -
 *       Spring Boot auto-configuration</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.jpa} -
 *       JPA-specific tenant context utilities</li>
 * </ul>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library;
