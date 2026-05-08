package com.gogidix.rapidassist.service.health.monitor.service.application;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.in.ReportServiceHealthCommand;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.out.ServiceHealthStore;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ReportServiceHealthUseCase implements ReportServiceHealthCommand {

    private final ServiceHealthStore store;
    private final ServiceHealthRetentionProperties retentionProperties;

    public ReportServiceHealthUseCase(ServiceHealthStore store, ServiceHealthRetentionProperties retentionProperties) {
        this.store = store;
        this.retentionProperties = retentionProperties;
    }

    @Override
    public void report(ServiceHealthReport report) {
        Instant checkedAt = report.checkedAt() == null ? Instant.now() : report.checkedAt();
        Instant expiresAt = checkedAt.plus(retentionProperties.getRetention());

        ServiceHealthReport normalized = new ServiceHealthReport(
                report.tenantId(),
                report.country(),
                report.serviceName(),
                report.instanceId(),
                report.status(),
                checkedAt,
                report.details()
        );

        store.append(normalized, expiresAt);
    }
}
