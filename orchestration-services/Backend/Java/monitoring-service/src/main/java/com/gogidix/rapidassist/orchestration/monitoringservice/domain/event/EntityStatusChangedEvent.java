package com.gogidix.rapidassist.orchestration.monitoringservice.domain.event;

public record EntityStatusChangedEvent(String entityId, String oldStatus, String newStatus) {}