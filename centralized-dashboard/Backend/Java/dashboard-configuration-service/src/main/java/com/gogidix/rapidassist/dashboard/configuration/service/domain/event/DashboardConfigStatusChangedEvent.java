package com.gogidix.rapidassist.dashboard.configuration.service.domain.event;

public record DashboardConfigStatusChangedEvent(String id, String oldStatus, String newStatus) {}