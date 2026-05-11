package com.gogidix.rapidassist.dashboard.reporting.service.domain.event;

public record DashboardReportingStatusChangedEvent(String id, String oldStatus, String newStatus) {}