package com.gogidix.rapidassist.dashboard.analytics.service.domain.event;

public record DashboardAnalyticsStatusChangedEvent(String id, String oldStatus, String newStatus) {}