package com.gogidix.rapidassist.service.health.monitor.service.domain.port.in;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;

public interface ReportServiceHealthCommand {

    void report(ServiceHealthReport report);
}
