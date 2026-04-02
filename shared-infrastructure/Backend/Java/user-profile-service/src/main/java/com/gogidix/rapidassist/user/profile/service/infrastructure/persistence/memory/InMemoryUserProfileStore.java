package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.memory;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Deprecated
public class InMemoryUserProfileStore implements UserProfileStore {

    private final ConcurrentHashMap<String, UserProfile> byTenantAndSubject = new ConcurrentHashMap<>();

    private String key(String tenantId, String userId) {
        return tenantId + "::" + userId;
    }

    @Override
    public Optional<UserProfile> find(String tenantId, String subject) {
        return Optional.ofNullable(byTenantAndSubject.get(key(tenantId, subject)));
    }

    @Override
    public UserProfile upsert(UserProfile profile) {
        byTenantAndSubject.put(key(profile.tenantId(), profile.userId()), profile);
        return profile;
    }

    @Override
    public CompletableFuture<UserProfile> save(UserProfile profile) {
        return CompletableFuture.completedFuture(upsert(profile));
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.id() != null && p.id().equals(id))
            .findFirst());
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findByUserId(String tenantId, String userId) {
        return CompletableFuture.completedFuture(Optional.ofNullable(byTenantAndSubject.get(key(tenantId, userId))));
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findByEmail(String tenantId, String email) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.email().equals(email))
            .findFirst());
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByRole(String tenantId, String role) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.roles().contains(role))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByStatus(String tenantId, UserProfile.UserStatus status) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.status().equals(status))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByRoles(String tenantId, List<String> roles) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.roles().stream().anyMatch(roles::contains))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByEmailPattern(String tenantId, String emailPattern) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.email().contains(emailPattern))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByNamePattern(String tenantId, String firstName, String lastName) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) &&
                (p.firstName() != null && p.firstName().contains(firstName)) ||
                (p.lastName() != null && p.lastName().contains(lastName)))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) &&
                (p.userId().contains(keyword) || p.email().contains(keyword) ||
                 (p.firstName() != null && p.firstName().contains(keyword)) ||
                 (p.lastName() != null && p.lastName().contains(keyword))))
            .toList());
    }

    @Override
    public CompletableFuture<List<UserProfile>> findActive(String tenantId) {
        return findByStatus(tenantId, UserProfile.UserStatus.ACTIVE);
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByPermission(String tenantId, String permission) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.permissions().contains(permission))
            .toList());
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            byTenantAndSubject.values().removeIf(p -> p.id() != null && p.id().equals(id));
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByUserId(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.remove(key(tenantId, userId)) != null);
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId))
            .count());
    }

    @Override
    public CompletableFuture<Long> countByStatus(String tenantId, UserProfile.UserStatus status) {
        return CompletableFuture.supplyAsync(() -> byTenantAndSubject.values().stream()
            .filter(p -> p.tenantId().equals(tenantId) && p.status().equals(status))
            .count());
    }
}
