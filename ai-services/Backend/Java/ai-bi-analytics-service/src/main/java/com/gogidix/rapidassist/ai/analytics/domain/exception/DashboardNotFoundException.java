package com.gogidix.rapidassist.ai.analytics.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a dashboard is not found
 */
public class DashboardNotFoundException extends AnalyticsException {

    public DashboardNotFoundException(UUID dashboardId) {
        super("Dashboard not found with ID: " + dashboardId);
    }

    public DashboardNotFoundException(String message) {
        super(message);
    }
}
