package com.gogidix.rapidassist.shared.request.context.library.jpa;

import java.util.Optional;

/**
 * JPA-specific tenant context utility that provides Long-based tenant ID access.
 * <p>
 * This class serves as a bridge between the string-based TenantContext in the
 * shared-request-context-library and the Long-based tenant IDs used in JPA
 * entities. It provides convenient methods for converting between the two
 * formats and accessing the current tenant ID in a type-safe manner.
 * </p>
 * <p>
 * All methods in this class are thread-safe as they delegate to the thread-local
 * RequestContextHolder.
 * </p>
 * <p>
 * Usage example:
 * <pre>{@code
 * // Get current tenant ID as Long
 * Long tenantId = JpaTenantContext.getTenantId().orElse(1L);
 *
 * // Require tenant ID or throw exception
 * Long currentTenant = JpaTenantContext.getTenantIdOrThrow();
 *
 * // Check if tenant is present
 * if (JpaTenantContext.isTenantPresent()) {
 *     // Process tenant-specific logic
 * }
 * }</pre>
 * </p>
 *
 * @author Gogidix Platform Team
 * @version 1.0
 * @since 2026-03-10
 */
public final class JpaTenantContext {

    private static final Long DEFAULT_TENANT = 1L;

    private JpaTenantContext() {
        // Utility class - prevent instantiation
    }

    /**
     * Retrieves the current tenant ID from the request context as a Long.
     * <p>
     * This method attempts to parse the string-based tenant ID from the shared
     * request context into a Long value. If parsing fails or the tenant ID is
     * not present, it returns an empty Optional.
     * </p>
     *
     * @return Optional containing the tenant ID as Long if present and valid,
     *         empty otherwise
     */
    public static Optional<Long> getTenantId() {
        return com.gogidix.rapidassist.shared.request.context.library.domain
                .TenantContext.getTenantId()
                .flatMap(JpaTenantContext::parseLong);
    }

    /**
     * Retrieves the current tenant ID from the request context as a Long.
     *
     * @return the tenant ID as Long
     * @throws com.gogidix.rapidassist.shared.request.context.library.exception
     *         .TenantNotFoundException if no tenant ID is present in the
     *         current context
     * @throws NumberFormatException if the tenant ID cannot be parsed as a Long
     */
    public static Long getTenantIdOrThrow() {
        String tenantId = com.gogidix.rapidassist.shared.request.context.library
                .domain.TenantContext.getTenantIdOrThrow();
        return parseLongOrThrow(tenantId);
    }

    /**
     * Retrieves the current tenant ID or returns the default tenant ID.
     *
     * @return the current tenant ID, or the default tenant ID (1L) if not present
     */
    public static Long getTenantIdOrDefault() {
        return getTenantId().orElse(DEFAULT_TENANT);
    }

    /**
     * Checks if a tenant ID is present in the current request context.
     *
     * @return true if a tenant ID is present, false otherwise
     */
    public static boolean isTenantPresent() {
        return com.gogidix.rapidassist.shared.request.context.library.domain.TenantContext.isTenantPresent();
    }

    /**
     * Sets the tenant ID in the current request context.
     *
     * @param tenantId the tenant ID to set
     * @throws IllegalArgumentException if tenantId is null
     */
    public static void setTenantId(final Long tenantId) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        com.gogidix.rapidassist.shared.request.context.library.domain
                .RequestContextHolder.set(
                        com.gogidix.rapidassist.shared.request.context.library
                                .domain.TenantContext.withTenantId(
                                        String.valueOf(tenantId))
        );
    }

    /**
     * Clears the current request context.
     */
    public static void clear() {
        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.clear();
    }

    /**
     * Executes the given action with the specified tenant ID.
     * <p>
     * This method temporarily sets the tenant ID in the current context,
     * executes the action, and restores the original context afterward.
     * </p>
     *
     * @param tenantId the tenant ID to use
     * @param action   the action to execute
     * @param <T>      the return type of the action
     * @return the result of the action
     * @throws IllegalArgumentException if tenantId is null
     */
    public static <T> T withTenantId(final Long tenantId,
                                     final java.util.function.Supplier<T> action) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID cannot be null");
        }
        return com.gogidix.rapidassist.shared.request.context.library
                .domain.TenantContext.withTenantId(
                        String.valueOf(tenantId),
                        action
        );
    }

    /**
     * Parses a string value to a Long, returning empty Optional if parsing fails.
     *
     * @param value the string value to parse
     * @return Optional containing the Long value if parsing succeeds, empty
     *         otherwise
     */
    private static Optional<Long> parseLong(final String value) {
        try {
            return Optional.of(Long.valueOf(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Parses a string value to a Long, throwing exception if parsing fails.
     *
     * @param value the string value to parse
     * @return the Long value
     * @throws NumberFormatException if the value cannot be parsed as a Long
     */
    private static Long parseLongOrThrow(final String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid tenant ID format: " + value);
        }
    }
}
