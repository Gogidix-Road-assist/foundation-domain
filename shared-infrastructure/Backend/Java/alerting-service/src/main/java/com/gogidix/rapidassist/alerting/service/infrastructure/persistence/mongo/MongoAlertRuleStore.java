package com.gogidix.rapidassist.alerting.service.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.alerting.service.domain.model.AlertRule;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertRuleStore;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.List;

public class MongoAlertRuleStore implements AlertRuleStore {

    private final MongoTemplate template;

    public MongoAlertRuleStore(MongoTemplate template) {
        this.template = template;
    }

    @Override
    public void upsert(AlertRule rule, Instant expiresAt) {
        template.save(toDocument(rule, expiresAt));
    }

    @Override
    public List<AlertRule> list(String tenantId, int limit) {
        Query q = new Query(Criteria.where("tenantId").is(tenantId))
                .with(Sort.by(Sort.Direction.DESC, "createdAt"))
                .limit(limit);

        return template.find(q, AlertRuleDocument.class).stream().map(MongoAlertRuleStore::toDomain).toList();
    }

    private static AlertRuleDocument toDocument(AlertRule rule, Instant expiresAt) {
        AlertRuleDocument doc = new AlertRuleDocument();
        doc.setTenantId(rule.tenantId());
        doc.setRuleId(rule.ruleId());
        doc.setName(rule.name());
        doc.setSeverity(rule.severity());
        doc.setEnabled(rule.enabled());
        doc.setConditions(rule.conditions());
        doc.setCreatedAt(rule.createdAt());
        doc.setExpiresAt(expiresAt);
        return doc;
    }

    private static AlertRule toDomain(AlertRuleDocument doc) {
        return new AlertRule(
                doc.getTenantId(),
                doc.getRuleId(),
                doc.getName(),
                doc.getSeverity(),
                doc.isEnabled(),
                doc.getConditions(),
                doc.getCreatedAt()
        );
    }
}
