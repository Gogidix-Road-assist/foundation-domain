package com.gogidix.rapidassist.orchestration.dispatching.shared.util;

import java.util.UUID;

/**
 * Utility class for generating IDs
 */
public final class IdGenerator {

    private IdGenerator() {}

    /**
     * Generate a unique dispatch ID
     */
    public static String generateDispatchId() {
        return "DSP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generate a unique assignment ID
     */
    public static String generateAssignmentId() {
        return "ASN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generate a unique tracking ID
     */
    public static String generateTrackingId() {
        return "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Generate a correlation ID
     */
    public static String generateCorrelationId() {
        return "COR-" + UUID.randomUUID().toString();
    }
}
