package com.gogidix.rapidassist.service.health.monitor.service.domain.port.out;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;

import java.time.Instant;
import java.util.List;

public interface ServiceHealthStore {

    void append(ServiceHealthReport report, Instant expiresAt);

    List<ServiceHealthReport> latest(String tenantId, String serviceName, int limit);
}
