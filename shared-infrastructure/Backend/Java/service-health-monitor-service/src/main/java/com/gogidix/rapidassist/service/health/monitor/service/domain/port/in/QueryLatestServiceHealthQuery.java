package com.gogidix.rapidassist.service.health.monitor.service.domain.port.in;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;

import java.util.List;

public interface QueryLatestServiceHealthQuery {

    List<ServiceHealthReport> latest(String tenantId, String serviceName, int limit);
}
