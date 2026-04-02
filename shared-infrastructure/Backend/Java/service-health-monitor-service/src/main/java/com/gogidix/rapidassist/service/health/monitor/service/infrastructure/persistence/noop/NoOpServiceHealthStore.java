package com.gogidix.rapidassist.service.health.monitor.service.infrastructure.persistence.noop;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.out.ServiceHealthStore;

import java.time.Instant;
import java.util.List;

public class NoOpServiceHealthStore implements ServiceHealthStore {

    @Override
    public void append(ServiceHealthReport report, Instant expiresAt) {
    }

    @Override
    public List<ServiceHealthReport> latest(String tenantId, String serviceName, int limit) {
        return List.of();
    }
}
