/**
 * Auto-configuration package for the shared request context library.
 *
 * <p>This package contains Spring Boot auto-configuration classes that
 * automatically configure the request context infrastructure for applications
 * using this library.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library
 *       .autoconfigure.SharedRequestContextAutoConfiguration} -
 *       Main auto-configuration class</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library
 *       .autoconfigure.RequestContextFilter} -
 *       Servlet filter that captures request context from HTTP headers</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library
 *       .autoconfigure.RequestContextProperties} -
 *       Configuration properties for the library</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library
 *       .autoconfigure.AsyncConfig} -
 *       Configuration for async task execution with context propagation</li>
 * </ul>
 *
 * <p>Configuration properties are prefixed with {@code gogidix.request-context}
 * .</p>
 *
 * <p>Example application.yml configuration:</p>
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
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;
