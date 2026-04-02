package com.gogidix.rapidassist.service.health.monitor.service.application;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.QueryLatestServiceHealthQuery;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.out.ServiceHealthStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryLatestServiceHealthUseCase implements QueryLatestServiceHealthQuery {

    private final ServiceHealthStore store;

    public QueryLatestServiceHealthUseCase(ServiceHealthStore store) {
        this.store = store;
    }

    @Override
    public List<ServiceHealthReport> latest(String tenantId, String serviceName, int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        return store.latest(tenantId, serviceName, safeLimit);
    }
}
