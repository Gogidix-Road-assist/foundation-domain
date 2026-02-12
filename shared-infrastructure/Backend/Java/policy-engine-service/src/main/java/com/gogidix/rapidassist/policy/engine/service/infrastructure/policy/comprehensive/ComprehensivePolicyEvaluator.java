package com.gogidix.rapidassist.policy.engine.service.infrastructure.policy.comprehensive;

import com.gogidix.rapidassist.policy.engine.service.domain.model.PolicyDecision;
import com.gogidix.rapidassist.policy.engine.service.domain.port.out.PolicyEvaluator;
import com.gogidix.rapidassist.policy.engine.service.infrastructure.policy.PolicyEngineProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Comprehensive policy evaluator supporting complex business rules,
 * conditions, and dynamic policy loading.
 */
public class ComprehensivePolicyEvaluator implements PolicyEvaluator {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensivePolicyEvaluator.class);

    private final MongoTemplate mongoTemplate;
    private final PolicyEngineProperties properties;

    // Cache for frequently accessed policies
    private final Map<String, CachedPolicy> policyCache = new HashMap<>();

    public ComprehensivePolicyEvaluator(MongoTemplate mongoTemplate, PolicyEngineProperties properties) {
        this.mongoTemplate = mongoTemplate;
        this.properties = properties;

        logger.info("ComprehensivePolicyEvaluator initialized with cache TTL: {} seconds",
                   properties.getCacheTtlSeconds());
    }

    @Override
    public PolicyDecision evaluate(String tenantId, String policyId, Map<String, Object> input) {
        if (tenantId == null || policyId == null || input == null) {
            return new PolicyDecision(false, policyId, "Invalid input parameters", Map.of());
        }

        try {
            // Get policy definition
            PolicyDefinition policy = getPolicy(tenantId, policyId);
            if (policy == null) {
                return new PolicyDecision(false, policyId, "Policy not found", Map.of());
            }

            // Check if policy is active and within validity period
            if (!policy.isActive()) {
                return new PolicyDecision(false, policyId, "Policy is inactive", Map.of());
            }

            if (policy.getValidFrom() != null && Instant.now().isBefore(policy.getValidFrom())) {
                return new PolicyDecision(false, policyId, "Policy is not yet valid", Map.of());
            }

            if (policy.getValidTo() != null && Instant.now().isAfter(policy.getValidTo())) {
                return new PolicyDecision(false, policyId, "Policy has expired", Map.of());
            }

            // Evaluate rules
            EvaluationResult result = evaluateRules(policy.getRules(), input);

            // Build decision
            Map<String, Object> decisionMetadata = new HashMap<>();
            decisionMetadata.put("policyVersion", policy.getVersion());
            decisionMetadata.put("evaluatedRules", result.getEvaluatedRules());
            decisionMetadata.put("evaluationTimeMs", result.getEvaluationTimeMs());

            if (result.isSuccess()) {
                decisionMetadata.put("appliedActions", result.getAppliedActions());
                decisionMetadata.put("matchedRuleIds", result.getMatchedRuleIds());
                logger.debug("Policy {} evaluated successfully with {} actions",
                           policyId, result.getAppliedActions().size());
            }

            return new PolicyDecision(result.isSuccess(), policyId,
                                     result.getMessage(), decisionMetadata);

        } catch (Exception e) {
            logger.error("Policy evaluation failed for tenant: {}, policy: {}",
                        tenantId, policyId, e);
            return new PolicyDecision(false, policyId,
                                     "Policy evaluation failed due to system error", Map.of());
        }
    }

    private PolicyDefinition getPolicy(String tenantId, String policyId) {
        String cacheKey = tenantId + ":" + policyId;
        CachedPolicy cached = policyCache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            return cached.getPolicy();
        }

        // Load from database
        Query query = new Query(Criteria.where("tenantId").is(tenantId)
                              .and("policyId").is(policyId)
                              .and("active").is(true))
                        .with(Sort.by(Sort.Direction.DESC, "version"))
                        .limit(1);

        PolicyDocument document = mongoTemplate.findOne(query, PolicyDocument.class);

        if (document != null) {
            PolicyDefinition policy = toPolicyDefinition(document);
            policyCache.put(cacheKey, new CachedPolicy(policy, Instant.now()));
            return policy;
        }

        return null;
    }

    private PolicyDefinition toPolicyDefinition(PolicyDocument doc) {
        List<PolicyRule> rules = new ArrayList<>();
        if (doc.getRules() != null) {
            for (PolicyRuleDocument ruleDoc : doc.getRules()) {
                rules.add(new PolicyRule(
                    ruleDoc.getId(),
                    ruleDoc.getName(),
                    ruleDoc.getDescription(),
                    convertToConditions(ruleDoc.getConditions()),
                    convertToActions(ruleDoc.getActions()),
                    ruleDoc.getPriority(),
                    ruleDoc.isEnabled()
                ));
            }
        }

        return new PolicyDefinition(
            doc.getPolicyId(),
            doc.getName(),
            doc.getDescription(),
            doc.getVersion(),
            doc.getTenantId(),
            rules,
            doc.isActive(),
            doc.getValidFrom(),
            doc.getValidTo(),
            doc.getMetadata()
        );
    }

    private EvaluationResult evaluateRules(List<PolicyRule> rules, Map<String, Object> input) {
        long startTime = System.currentTimeMillis();
        List<String> evaluatedRules = new ArrayList<>();
        List<String> matchedRuleIds = new ArrayList<>();
        List<PolicyAction> appliedActions = new ArrayList<>();

        try {
            // Sort rules by priority (lower number = higher priority)
            rules.sort(Comparator.comparingInt(PolicyRule::getPriority));

            for (PolicyRule rule : rules) {
                if (!rule.isEnabled()) {
                    continue;
                }

                evaluatedRules.add(rule.getId());

                if (evaluateConditions(rule.getConditions(), input)) {
                    matchedRuleIds.add(rule.getId());

                    // Apply actions
                    for (PolicyAction action : rule.getActions()) {
                        if (evaluateCondition(action.getCondition(), input)) {
                            appliedActions.add(action);
                            // For deny actions, return immediately
                            if ("DENY".equals(action.getType())) {
                                return new EvaluationResult(
                                    false,
                                    "Access denied by rule: " + rule.getName(),
                                    appliedActions,
                                    matchedRuleIds,
                                    evaluatedRules,
                                    System.currentTimeMillis() - startTime
                                );
                            }
                        }
                    }
                }
            }

            // If we reached here and have allow actions, grant access
            boolean hasAllowAction = appliedActions.stream()
                    .anyMatch(a -> "ALLOW".equals(a.getType()));

            if (hasAllowAction) {
                return new EvaluationResult(
                    true,
                    "Access granted by policy rules",
                    appliedActions,
                    matchedRuleIds,
                    evaluatedRules,
                    System.currentTimeMillis() - startTime
                );
            }

            // Default deny
            return new EvaluationResult(
                false,
                "No matching policy rules found",
                appliedActions,
                matchedRuleIds,
                evaluatedRules,
                System.currentTimeMillis() - startTime
            );

        } catch (Exception e) {
            logger.error("Rule evaluation failed", e);
            return new EvaluationResult(
                false,
                "Rule evaluation failed: " + e.getMessage(),
                appliedActions,
                matchedRuleIds,
                evaluatedRules,
                System.currentTimeMillis() - startTime
            );
        }
    }

    private boolean evaluateConditions(List<PolicyCondition> conditions, Map<String, Object> input) {
        if (conditions == null || conditions.isEmpty()) {
            return true;
        }

        for (PolicyCondition condition : conditions) {
            if (!evaluateCondition(condition, input)) {
                return false;
            }
        }

        return true;
    }

    private boolean evaluateCondition(PolicyCondition condition, Map<String, Object> input) {
        Object value = getInputValue(input, condition.getAttribute());
        return compareValues(value, condition.getOperator(), condition.getValue());
    }

    private Object getInputValue(Map<String, Object> input, String attributePath) {
        String[] path = attributePath.split("\\.");
        Object current = input;

        for (String segment : path) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(segment);
            } else {
                return null;
            }
        }

        return current;
    }

    private boolean compareValues(Object actual, String operator, Object expected) {
        switch (operator.toUpperCase()) {
            case "EQUALS":
                return Objects.equals(actual, expected);
            case "NOT_EQUALS":
                return !Objects.equals(actual, expected);
            case "GREATER_THAN":
                return compareNumbers(actual, expected) > 0;
            case "GREATER_THAN_OR_EQUAL":
                return compareNumbers(actual, expected) >= 0;
            case "LESS_THAN":
                return compareNumbers(actual, expected) < 0;
            case "LESS_THAN_OR_EQUAL":
                return compareNumbers(actual, expected) <= 0;
            case "CONTAINS":
                return actual != null && actual.toString().contains(expected.toString());
            case "NOT_CONTAINS":
                return actual == null || !actual.toString().contains(expected.toString());
            case "STARTS_WITH":
                return actual != null && actual.toString().startsWith(expected.toString());
            case "ENDS_WITH":
                return actual != null && actual.toString().endsWith(expected.toString());
            case "MATCHES":
                return actual != null && Pattern.matches(expected.toString(), actual.toString());
            case "IN":
                if (expected instanceof Collection) {
                    return ((Collection<?>) expected).contains(actual);
                }
                return false;
            case "NOT_IN":
                if (expected instanceof Collection) {
                    return !((Collection<?>) expected).contains(actual);
                }
                return false;
            case "IS_NULL":
                return actual == null;
            case "IS_NOT_NULL":
                return actual != null;
            case "IS_EMPTY":
                return actual == null || (actual instanceof Collection && ((Collection<?>) actual).isEmpty());
            case "IS_NOT_EMPTY":
                return actual != null && !(actual instanceof Collection && ((Collection<?>) actual).isEmpty());
            default:
                logger.warn("Unknown operator: {}", operator);
                return false;
        }
    }

    @SuppressWarnings("unchecked")
    private int compareNumbers(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            return Double.compare(((Number) a).doubleValue(), ((Number) b).doubleValue());
        }
        if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable<Object>) a).compareTo(b);
        }
        return a.toString().compareTo(b.toString());
    }

    // Data models
    private static class CachedPolicy {
        private final PolicyDefinition policy;
        private final Instant cachedAt;

        CachedPolicy(PolicyDefinition policy, Instant cachedAt) {
            this.policy = policy;
            this.cachedAt = cachedAt;
        }

        PolicyDefinition getPolicy() {
            return policy;
        }

        boolean isExpired() {
            return cachedAt.plusSeconds(300).isBefore(Instant.now()); // 5 minute cache
        }
    }

    private static class EvaluationResult {
        private final boolean success;
        private final String message;
        private final List<PolicyAction> appliedActions;
        private final List<String> matchedRuleIds;
        private final List<String> evaluatedRules;
        private final long evaluationTimeMs;

        EvaluationResult(boolean success, String message, List<PolicyAction> appliedActions,
                        List<String> matchedRuleIds, List<String> evaluatedRules, long evaluationTimeMs) {
            this.success = success;
            this.message = message;
            this.appliedActions = appliedActions;
            this.matchedRuleIds = matchedRuleIds;
            this.evaluatedRules = evaluatedRules;
            this.evaluationTimeMs = evaluationTimeMs;
        }

        boolean isSuccess() { return success; }
        String getMessage() { return message; }
        List<PolicyAction> getAppliedActions() { return appliedActions; }
        List<String> getMatchedRuleIds() { return matchedRuleIds; }
        List<String> getEvaluatedRules() { return evaluatedRules; }
        long getEvaluationTimeMs() { return evaluationTimeMs; }
    }

    // Policy domain models
    private static class PolicyDefinition {
        private final String policyId;
        private final String name;
        private final String description;
        private final String version;
        private final String tenantId;
        private final List<PolicyRule> rules;
        private final boolean active;
        private final Instant validFrom;
        private final Instant validTo;
        private final Map<String, Object> metadata;

        PolicyDefinition(String policyId, String name, String description, String version,
                        String tenantId, List<PolicyRule> rules, boolean active,
                        Instant validFrom, Instant validTo, Map<String, Object> metadata) {
            this.policyId = policyId;
            this.name = name;
            this.description = description;
            this.version = version;
            this.tenantId = tenantId;
            this.rules = rules;
            this.active = active;
            this.validFrom = validFrom;
            this.validTo = validTo;
            this.metadata = metadata;
        }

        String getPolicyId() { return policyId; }
        String getName() { return name; }
        String getDescription() { return description; }
        String getVersion() { return version; }
        String getTenantId() { return tenantId; }
        List<PolicyRule> getRules() { return rules; }
        boolean isActive() { return active; }
        Instant getValidFrom() { return validFrom; }
        Instant getValidTo() { return validTo; }
        Map<String, Object> getMetadata() { return metadata; }
    }

    private static class PolicyRule {
        private final String id;
        private final String name;
        private final String description;
        private final List<PolicyCondition> conditions;
        private final List<PolicyAction> actions;
        private final int priority;
        private final boolean enabled;

        PolicyRule(String id, String name, String description, List<PolicyCondition> conditions,
                  List<PolicyAction> actions, int priority, boolean enabled) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.conditions = conditions;
            this.actions = actions;
            this.priority = priority;
            this.enabled = enabled;
        }

        String getId() { return id; }
        String getName() { return name; }
        String getDescription() { return description; }
        List<PolicyCondition> getConditions() { return conditions; }
        List<PolicyAction> getActions() { return actions; }
        int getPriority() { return priority; }
        boolean isEnabled() { return enabled; }
    }

    private static class PolicyCondition {
        private final String attribute;
        private final String operator;
        private final Object value;

        PolicyCondition(String attribute, String operator, Object value) {
            this.attribute = attribute;
            this.operator = operator;
            this.value = value;
        }

        String getAttribute() { return attribute; }
        String getOperator() { return operator; }
        Object getValue() { return value; }
    }

    private static class PolicyAction {
        private final String type;
        private final String name;
        private final Map<String, Object> parameters;
        private final PolicyCondition condition;

        PolicyAction(String type, String name, Map<String, Object> parameters, PolicyCondition condition) {
            this.type = type;
            this.name = name;
            this.parameters = parameters;
            this.condition = condition;
        }

        String getType() { return type; }
        String getName() { return name; }
        Map<String, Object> getParameters() { return parameters; }
        PolicyCondition getCondition() { return condition; }
    }

    // Helper methods to convert from MongoDB documents to domain models
    @SuppressWarnings("unchecked")
    private List<PolicyCondition> convertToConditions(List<Map<String, Object>> conditionMaps) {
        List<PolicyCondition> conditions = new ArrayList<>();
        if (conditionMaps != null) {
            for (Map<String, Object> map : conditionMaps) {
                conditions.add(new PolicyCondition(
                    (String) map.get("attribute"),
                    (String) map.get("operator"),
                    map.get("value")
                ));
            }
        }
        return conditions;
    }

    @SuppressWarnings("unchecked")
    private List<PolicyAction> convertToActions(List<Map<String, Object>> actionMaps) {
        List<PolicyAction> actions = new ArrayList<>();
        if (actionMaps != null) {
            for (Map<String, Object> map : actionMaps) {
                PolicyCondition condition = null;
                if (map.containsKey("condition")) {
                    Map<String, Object> condMap = (Map<String, Object>) map.get("condition");
                    condition = new PolicyCondition(
                        (String) condMap.get("attribute"),
                        (String) condMap.get("operator"),
                        condMap.get("value")
                    );
                }

                actions.add(new PolicyAction(
                    (String) map.get("type"),
                    (String) map.get("name"),
                    (Map<String, Object>) map.getOrDefault("parameters", Map.of()),
                    condition
                ));
            }
        }
        return actions;
    }

    // MongoDB document models
    private static class PolicyDocument {
        private String policyId;
        private String name;
        private String description;
        private String version;
        private String tenantId;
        private List<PolicyRuleDocument> rules;
        private boolean active;
        private Instant validFrom;
        private Instant validTo;
        private Map<String, Object> metadata;

        // Getters
        String getPolicyId() { return policyId; }
        String getName() { return name; }
        String getDescription() { return description; }
        String getVersion() { return version; }
        String getTenantId() { return tenantId; }
        List<PolicyRuleDocument> getRules() { return rules; }
        boolean isActive() { return active; }
        Instant getValidFrom() { return validFrom; }
        Instant getValidTo() { return validTo; }
        Map<String, Object> getMetadata() { return metadata; }
    }

    private static class PolicyRuleDocument {
        private String id;
        private String name;
        private String description;
        private List<Map<String, Object>> conditions;
        private List<Map<String, Object>> actions;
        private int priority;
        private boolean enabled;

        // Getters
        String getId() { return id; }
        String getName() { return name; }
        String getDescription() { return description; }
        List<Map<String, Object>> getConditions() { return conditions; }
        List<Map<String, Object>> getActions() { return actions; }
        int getPriority() { return priority; }
        boolean isEnabled() { return enabled; }
    }
}