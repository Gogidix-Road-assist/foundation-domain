package com.gogidix.rapidassist.feature.flags.service.application;

import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlag;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagChange;
import com.gogidix.rapidassist.feature.flags.service.domain.model.FeatureFlagEvaluation;
import com.gogidix.rapidassist.feature.flags.service.domain.port.in.FeatureFlagCommand;
import com.gogidix.rapidassist.feature.flags.service.domain.port.in.FeatureFlagQuery;
import com.gogidix.rapidassist.feature.flags.service.domain.port.out.FeatureFlagRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class FeatureFlagService implements FeatureFlagCommand, FeatureFlagQuery {

    private static final Logger logger = LoggerFactory.getLogger(FeatureFlagService.class);

    @Autowired
    private FeatureFlagRepository repository;

    @Override
    public CompletableFuture<FeatureFlag> createFlag(CreateFlagCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Check if flag already exists
                Optional<FeatureFlag> existing = repository.findByKey(
                    command.tenantId(), command.key(), command.environment()).join();

                if (existing.isPresent()) {
                    throw new IllegalStateException("Feature flag already exists with key: " + command.key());
                }

                FeatureFlag flag = FeatureFlag.builder()
                    .tenantId(command.tenantId())
                    .key(command.key())
                    .name(command.name())
                    .description(command.description())
                    .type(command.type())
                    .rolloutStrategy(command.rolloutStrategy())
                    .environment(command.environment())
                    .createdBy(command.createdBy())
                    .tags(command.tags())
                    .requiresApproval(command.requiresApproval())
                    .expiresAt(command.expiresAt())
                    .allowedTenants(command.allowedTenants())
                    .allowedUsers(command.allowedUsers())
                    .allowedCountries(command.allowedCountries())
                    .percentageRollout(command.percentageRollout())
                    .status(FeatureFlag.FlagStatus.DRAFT)
                    .enabled(false)
                    .build();

                FeatureFlag saved = repository.save(flag).join();

                FeatureFlagChange change = FeatureFlagChange.create(
                    saved.id(), saved.key(), saved.tenantId(),
                    FeatureFlagChange.ChangeType.CREATE,
                    null, false,
                    command.createdBy(), "Feature flag created"
                );
                repository.saveChange(change).join();

                repository.cacheFlag(saved).join();

                logger.info("Created feature flag: {} for tenant: {}", command.key(), command.tenantId());
                return saved;

            } catch (Exception e) {
                logger.error("Error creating feature flag", e);
                throw new RuntimeException("Failed to create feature flag", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> updateFlag(UpdateFlagCommand command) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(command.flagId()).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();

                FeatureFlag updated = existing.toBuilder()
                    .name(command.name())
                    .description(command.description())
                    .type(command.type())
                    .rolloutStrategy(command.rolloutStrategy())
                    .updatedBy(command.updatedBy())
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(updated).join();

                FeatureFlagChange change = FeatureFlagChange.create(
                    saved.id(), saved.key(), saved.tenantId(),
                    FeatureFlagChange.ChangeType.UPDATE,
                    existing.enabled(), saved.enabled(),
                    command.updatedBy(), command.reason()
                );
                repository.saveChange(change).join();

                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                logger.info("Updated feature flag: {}", command.flagId());
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating feature flag", e);
                throw new RuntimeException("Failed to update feature flag", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> enableFlag(String flagId, String changedBy, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();

                FeatureFlag enabled = existing.enable()
                    .toBuilder()
                    .updatedBy(changedBy)
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(enabled).join();

                FeatureFlagChange change = FeatureFlagChange.create(
                    saved.id(), saved.key(), saved.tenantId(),
                    FeatureFlagChange.ChangeType.ENABLE,
                    false, true,
                    changedBy, reason != null ? reason : "Feature flag enabled"
                );
                repository.saveChange(change).join();

                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                logger.info("Enabled feature flag: {}", flagId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error enabling feature flag", e);
                throw new RuntimeException("Failed to enable feature flag", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> disableFlag(String flagId, String changedBy, String reason) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();

                FeatureFlag disabled = existing.disable()
                    .toBuilder()
                    .updatedBy(changedBy)
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(disabled).join();

                FeatureFlagChange change = FeatureFlagChange.create(
                    saved.id(), saved.key(), saved.tenantId(),
                    FeatureFlagChange.ChangeType.DISABLE,
                    true, false,
                    changedBy, reason != null ? reason : "Feature flag disabled"
                );
                repository.saveChange(change).join();

                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                logger.info("Disabled feature flag: {}", flagId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error disabling feature flag", e);
                throw new RuntimeException("Failed to disable feature flag", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> archiveFlag(String flagId, String changedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();

                FeatureFlag archived = existing.archive()
                    .toBuilder()
                    .updatedBy(changedBy)
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(archived).join();

                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                logger.info("Archived feature flag: {}", flagId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error archiving feature flag", e);
                throw new RuntimeException("Failed to archive feature flag", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> updateRolloutPercentage(String flagId, int percentage, String changedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();

                FeatureFlag updated = existing.toBuilder()
                    .percentageRollout(new FeatureFlag.PercentageRollout(percentage, "userId"))
                    .rolloutStrategy(FeatureFlag.RolloutStrategy.PERCENTAGE)
                    .updatedBy(changedBy)
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(updated).join();
                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                logger.info("Updated rollout percentage to {}% for flag: {}", percentage, flagId);
                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error updating rollout percentage", e);
                throw new RuntimeException("Failed to update rollout percentage", e);
            }
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> addAllowedUsers(String flagId, Set<String> userIds, String changedBy) {
        return modifyAllowedSet(flagId, changedBy, (flag) -> {
            Set<String> updated = new java.util.HashSet<>(flag.allowedUsers());
            updated.addAll(userIds);
            return flag.toBuilder().allowedUsers(updated).build();
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> removeAllowedUsers(String flagId, Set<String> userIds, String changedBy) {
        return modifyAllowedSet(flagId, changedBy, (flag) -> {
            Set<String> updated = new java.util.HashSet<>(flag.allowedUsers());
            updated.removeAll(userIds);
            return flag.toBuilder().allowedUsers(updated).build();
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> addAllowedTenants(String flagId, Set<String> tenantIds, String changedBy) {
        return modifyAllowedSet(flagId, changedBy, (flag) -> {
            Set<String> updated = new java.util.HashSet<>(flag.allowedTenants());
            updated.addAll(tenantIds);
            return flag.toBuilder().allowedTenants(updated).build();
        });
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> addAllowedCountries(String flagId, Set<String> countryCodes, String changedBy) {
        return modifyAllowedSet(flagId, changedBy, (flag) -> {
            Set<String> updated = new java.util.HashSet<>(flag.allowedCountries());
            updated.addAll(countryCodes);
            return flag.toBuilder().allowedCountries(updated).build();
        });
    }

    private CompletableFuture<Optional<FeatureFlag>> modifyAllowedSet(String flagId, String changedBy,
                                                                        java.util.function.Function<FeatureFlag, FeatureFlag> modifier) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return Optional.empty();
                }

                FeatureFlag existing = flagOpt.get();
                FeatureFlag updated = modifier.apply(existing)
                    .toBuilder()
                    .updatedBy(changedBy)
                    .version(existing.version() + 1)
                    .build();

                FeatureFlag saved = repository.save(updated).join();
                repository.evictFlag(saved.tenantId(), saved.key(), saved.environment()).join();

                return Optional.of(saved);

            } catch (Exception e) {
                logger.error("Error modifying allowed set", e);
                throw new RuntimeException("Failed to modify allowed set", e);
            }
        });
    }

    @Override
    public CompletableFuture<FeatureFlagChange> approveChange(String changeId, String approvedBy) {
        return repository.findChangeById(changeId)
            .thenCompose(changeOpt -> {
                if (changeOpt.isEmpty()) {
                    throw new IllegalArgumentException("Change not found: " + changeId);
                }
                FeatureFlagChange approved = changeOpt.get().approve(approvedBy);
                return repository.saveChange(approved);
            });
    }

    @Override
    public CompletableFuture<FeatureFlagChange> rejectChange(String changeId, String rejectedBy, String reason) {
        return repository.findChangeById(changeId)
            .thenCompose(changeOpt -> {
                if (changeOpt.isEmpty()) {
                    throw new IllegalArgumentException("Change not found: " + changeId);
                }
                FeatureFlagChange rejected = changeOpt.get().reject(rejectedBy);
                return repository.saveChange(rejected);
            });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> bulkEnable(List<String> flagIds, String changedBy) {
        return CompletableFuture.supplyAsync(() -> {
            List<FeatureFlag> results = flagIds.stream()
                .map(id -> enableFlag(id, changedBy, "Bulk enable").join())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            logger.info("Bulk enabled {} feature flags", results.size());
            return results;
        });
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> bulkDisable(List<String> flagIds, String changedBy) {
        return CompletableFuture.supplyAsync(() -> {
            List<FeatureFlag> results = flagIds.stream()
                .map(id -> disableFlag(id, changedBy, "Bulk disable").join())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            logger.info("Bulk disabled {} feature flags", results.size());
            return results;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteFlag(String flagId, String deletedBy) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Optional<FeatureFlag> flagOpt = repository.findById(flagId).join();
                if (flagOpt.isEmpty()) {
                    return false;
                }

                FeatureFlag flag = flagOpt.get();
                if (flag.status() == FeatureFlag.FlagStatus.ACTIVE && flag.enabled()) {
                    throw new IllegalStateException("Cannot delete active enabled flag");
                }

                boolean deleted = repository.deleteById(flagId).join();
                repository.evictFlag(flag.tenantId(), flag.key(), flag.environment()).join();

                logger.info("Deleted feature flag: {}", flagId);
                return deleted;

            } catch (Exception e) {
                logger.error("Error deleting feature flag", e);
                return false;
            }
        });
    }

    // Query implementation

    @Override
    public CompletableFuture<FeatureFlagEvaluation> evaluateFlag(String flagKey, String tenantId, EvaluationContext context) {
        return repository.getCachedFlag(tenantId, flagKey, context.environment())
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached.get())
                : repository.findByKey(tenantId, flagKey, context.environment())
                    .thenApply(opt -> opt.map(flag -> {
                        repository.cacheFlag(flag);
                        return flag;
                    }).orElse(null)))
            .thenApply(flag -> {
                if (flag == null) {
                    return FeatureFlagEvaluation.notFound(flagKey, tenantId);
                }

                if (!flag.enabled() || !flag.isActive()) {
                    return FeatureFlagEvaluation.disabled(flagKey, tenantId, context.userId());
                }

                boolean enabled = evaluateForContext(flag, context);
                String variant = enabled ? "default" : null;

                return FeatureFlagEvaluation.builder()
                    .flagKey(flagKey)
                    .tenantId(tenantId)
                    .userId(context.userId())
                    .enabled(enabled)
                    .variant(variant)
                    .reason(enabled ? "Feature is enabled for context" : "Feature is disabled for context")
                    .build();
            });
    }

    private boolean evaluateForContext(FeatureFlag flag, EvaluationContext context) {
        return switch (flag.rolloutStrategy()) {
            case ALL_USERS -> true;
            case SPECIFIC_TENANTS -> flag.allowedTenants().contains(context.tenantId());
            case SPECIFIC_USERS -> flag.allowedUsers().contains(context.userId());
            case COUNTRY_BASED -> context.countryCode() != null && flag.allowedCountries().contains(context.countryCode());
            case PERCENTAGE -> {
                if (flag.percentageRollout() == null) {
                    yield false;
                }
                int bucket = getBucket(context.userId() + flag.key(), 100);
                yield bucket < flag.percentageRollout().percentage();
            }
            case GRADUAL -> {
                // Simple gradual rollout - can be enhanced with time-based logic
                if (flag.percentageRollout() == null) {
                    yield false;
                }
                int bucket = getBucket(context.userId() + flag.key(), 100);
                yield bucket < flag.percentageRollout().percentage();
            }
        };
    }

    private int getBucket(String input, int buckets) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            long value = ((long) (hash[0] & 0xFF) << 24) |
                        ((long) (hash[1] & 0xFF) << 16) |
                        ((long) (hash[2] & 0xFF) << 8) |
                        (hash[3] & 0xFF);
            return (int) ((value & 0xFFFFFFFFL) % buckets);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public CompletableFuture<List<FeatureFlagEvaluation>> evaluateAllFlags(String tenantId, EvaluationContext context) {
        return repository.findByTenantAndEnvironment(tenantId, context.environment())
            .thenApply(flags -> flags.stream()
                .map(flag -> evaluateFlag(flag.key(), tenantId, context).join())
                .toList());
    }

    @Override
    public CompletableFuture<Boolean> isFlagEnabled(String flagKey, String tenantId, String userId) {
        return evaluateFlag(flagKey, tenantId, EvaluationContext.of(userId, tenantId))
            .thenApply(FeatureFlagEvaluation::enabled);
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> getFlagById(String flagId) {
        return repository.findById(flagId);
    }

    @Override
    public CompletableFuture<Optional<FeatureFlag>> getFlagByKey(String tenantId, String key, String environment) {
        return repository.getCachedFlag(tenantId, key, environment)
            .thenCompose(cached -> cached.isPresent()
                ? CompletableFuture.completedFuture(cached)
                : repository.findByKey(tenantId, key, environment));
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getAllFlags(String tenantId) {
        return repository.findByTenant(tenantId);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getFlagsByEnvironment(String tenantId, String environment) {
        return repository.findByTenantAndEnvironment(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getFlagsByStatus(String tenantId, FeatureFlag.FlagStatus status) {
        return repository.findByStatus(tenantId, status);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getActiveFlags(String tenantId, String environment) {
        return repository.findAllActive(tenantId, environment);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getFlagsByTags(String tenantId, Set<String> tags) {
        return repository.findByTags(tenantId, tags);
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> getFlagHistory(String flagId) {
        return repository.findChangesByFlagId(flagId);
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> getChangesByTimeRange(String tenantId, Instant from, Instant to) {
        return repository.findChangesByTimeRange(tenantId, from, to);
    }

    @Override
    public CompletableFuture<List<FeatureFlagChange>> getPendingApprovals(String tenantId) {
        return repository.findPendingApprovals(tenantId);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> searchFlags(String tenantId, String keyword) {
        return repository.searchByKeyword(tenantId, keyword);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getFlagsCreatedSince(String tenantId, Instant since) {
        return repository.findCreatedSince(tenantId, since);
    }

    @Override
    public CompletableFuture<List<FeatureFlag>> getFlagsExpiringBefore(String tenantId, Instant before) {
        return repository.findExpiringBefore(tenantId, before);
    }

    @Override
    public CompletableFuture<FlagStatistics> getStatistics(String tenantId) {
        return repository.findByTenant(tenantId)
            .thenApply(flags -> {
                long activeFlags = flags.stream().filter(f -> f.status() == FeatureFlag.FlagStatus.ACTIVE).count();
                long enabledFlags = flags.stream().filter(FeatureFlag::enabled).count();
                long expiringSoon = flags.stream()
                    .filter(f -> f.expiresAt() != null && f.expiresAt().isBefore(Instant.now().plusSeconds(86400 * 7)))
                    .count();
                long requiringApproval = flags.stream()
                    .filter(f -> Boolean.TRUE.equals(f.requiresApproval()))
                    .count();

                return new FlagStatistics(
                    flags.size(),
                    activeFlags,
                    enabledFlags,
                    flags.size() - enabledFlags,
                    expiringSoon,
                    requiringApproval
                );
            });
    }
}
