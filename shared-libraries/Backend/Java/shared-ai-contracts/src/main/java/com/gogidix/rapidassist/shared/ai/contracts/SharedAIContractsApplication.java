package com.gogidix.rapidassist.shared.ai.contracts;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for Shared AI Contracts library.
 *
 * This is a library module and not intended to be run as a standalone application.
 * The @SpringBootApplication annotation is present for Spring Boot auto-configuration
 * and component scanning during testing.
 *
 * Implementing services should include this dependency to gain access to:
 * - IAIExecutionPort: The main AI service contract
 * - AIRequest/AIResponse: Base request/response models
 * - AIExecutionContext: Extended tenant execution context
 * - AIObservabilityPort: Observability integration hooks
 * - AIExecutionException: Standardized exception handling
 */
@SpringBootApplication
public class SharedAIContractsApplication {

    public static void main(String[] args) {
        // This library should not be run as a standalone application
        throw new UnsupportedOperationException(
                "Shared AI Contracts is a library module and should not be run as a standalone application. " +
                "Include this as a dependency in your AI service implementation.");
    }
}
