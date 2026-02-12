package com.gogidix.rapidassist.orchestration.fleet_policy.infrastructure.messaging.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;
import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.PolicyViolation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka event publisher for Policy events
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PolicyEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String POLICY_TOPIC = "fleet.policy.events";
    private static final String VIOLATION_TOPIC = "fleet.policy.violations";

    /**
     * Publish policy created event
     */
    public void publishPolicyCreated(Policy policy) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "POLICY_CREATED");
            event.put("policyId", policy.getId());
            event.put("policyCode", policy.getPolicyCode());
            event.put("tenantId", policy.getTenantId());
            event.put("policyType", policy.getPolicyType().name());
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(POLICY_TOPIC, policy.getId(), message);

            log.info("Published policy created event: {}", policy.getPolicyCode());
        } catch (Exception e) {
            log.error("Error publishing policy created event", e);
        }
    }

    /**
     * Publish policy updated event
     */
    public void publishPolicyUpdated(Policy policy) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "POLICY_UPDATED");
            event.put("policyId", policy.getId());
            event.put("policyCode", policy.getPolicyCode());
            event.put("tenantId", policy.getTenantId());
            event.put("policyType", policy.getPolicyType().name());
            event.put("status", policy.getStatus().name());
            event.put("version", policy.getVersion());
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(POLICY_TOPIC, policy.getId(), message);

            log.info("Published policy updated event: {}", policy.getPolicyCode());
        } catch (Exception e) {
            log.error("Error publishing policy updated event", e);
        }
    }

    /**
     * Publish policy activated event
     */
    public void publishPolicyActivated(Policy policy) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "POLICY_ACTIVATED");
            event.put("policyId", policy.getId());
            event.put("policyCode", policy.getPolicyCode());
            event.put("tenantId", policy.getTenantId());
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(POLICY_TOPIC, policy.getId(), message);

            log.info("Published policy activated event: {}", policy.getPolicyCode());
        } catch (Exception e) {
            log.error("Error publishing policy activated event", e);
        }
    }

    /**
     * Publish violation detected event
     */
    public void publishViolationDetected(PolicyViolation violation) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "VIOLATION_DETECTED");
            event.put("violationId", violation.getId());
            event.put("policyId", violation.getPolicyId());
            event.put("policyCode", violation.getPolicyCode());
            event.put("tenantId", violation.getTenantId());
            event.put("entityType", violation.getEntityType().name());
            event.put("entityId", violation.getEntityId());
            event.put("severity", violation.getSeverity().name());
            event.put("points", violation.getPoints());
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(VIOLATION_TOPIC, violation.getId(), message);

            log.info("Published violation detected event: {}", violation.getRuleCode());
        } catch (Exception e) {
            log.error("Error publishing violation detected event", e);
        }
    }

    /**
     * Publish violation resolved event
     */
    public void publishViolationResolved(PolicyViolation violation) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "VIOLATION_RESOLVED");
            event.put("violationId", violation.getId());
            event.put("policyId", violation.getPolicyId());
            event.put("tenantId", violation.getTenantId());
            event.put("entityId", violation.getEntityId());
            event.put("resolvedBy", violation.getResolvedBy());
            event.put("resolvedAt", violation.getResolvedAt());
            event.put("timestamp", System.currentTimeMillis());

            String message = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(VIOLATION_TOPIC, violation.getId(), message);

            log.info("Published violation resolved event: {}", violation.getRuleCode());
        } catch (Exception e) {
            log.error("Error publishing violation resolved event", e);
        }
    }
}
