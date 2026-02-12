package com.gogidix.rapidassist.service.health.monitor.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.service.health.monitor.service.domain.model.ServiceHealthReport;
import com.gogidix.rapidassist.service.health.monitor.service.domain.port.out.ServiceHealthStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoServiceHealthStore implements ServiceHealthStore {

    private final MongoTemplate template;

    public MongoServiceHealthStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void append(ServiceHealthReport report, Instant expiresAt) {
        template.save(toDocument(report, expiresAt));
    }

    @Override
    public List<ServiceHealthReport> latest(String tenantId, String serviceName, int limit) {
        Criteria criteria = Criteria.where("tenantId").is(tenantId);
        if (serviceName != null && !serviceName.isBlank()) {
            criteria = criteria.and("serviceName").is(serviceName);
        }

        Query q = new Query(criteria)
                .with(Sort.by(Sort.Direction.DESC, "checkedAt"))
                .limit(limit);

        return template.find(q, ServiceHealthDocument.class).stream().map(MongoServiceHealthStore::toDomain).toList();
    }

    private static ServiceHealthDocument toDocument(ServiceHealthReport report, Instant expiresAt) {
        ServiceHealthDocument doc = new ServiceHealthDocument();
        doc.setTenantId(report.tenantId());
        doc.setCountry(report.country());
        doc.setServiceName(report.serviceName());
        doc.setInstanceId(report.instanceId());
        doc.setStatus(report.status());
        doc.setCheckedAt(report.checkedAt());
        doc.setDetails(report.details());
        doc.setExpiresAt(expiresAt);
        return doc;
    }

    private static ServiceHealthReport toDomain(ServiceHealthDocument doc) {
        return new ServiceHealthReport(
                doc.getTenantId(),
                doc.getCountry(),
                doc.getServiceName(),
                doc.getInstanceId(),
                doc.getStatus(),
                doc.getCheckedAt(),
                doc.getDetails()
        );
    }
}
