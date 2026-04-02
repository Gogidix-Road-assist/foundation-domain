package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.user.profile.service.domain.model.UserProfile;
import com.gogidix.rapidassist.user.profile.service.domain.port.out.UserProfileStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class MongoUserProfileStore implements UserProfileStore {

    private static final Logger logger = LoggerFactory.getLogger(MongoUserProfileStore.class);

    @Autowired
    private UserProfileRepository repository;

    @Override
    public CompletableFuture<UserProfile> save(UserProfile profile) {
        return CompletableFuture.supplyAsync(() -> {
            UserProfileDocument document = UserProfileDocument.fromDomain(profile);
            UserProfileDocument saved = repository.save(document);
            logger.debug("Saved user profile: {}", saved.userId());
            return saved.toDomain();
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findById(id)
                .map(UserProfileDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findByUserId(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndUserId(tenantId, userId)
                .map(UserProfileDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<Optional<UserProfile>> findByEmail(String tenantId, String email) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndEmail(tenantId, email)
                .map(UserProfileDocument::toDomain);
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantId(tenantId).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByRole(String tenantId, String role) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndRolesContaining(tenantId, role).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByStatus(String tenantId, UserProfile.UserStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndStatus(tenantId, status.name()).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByRoles(String tenantId, List<String> roles) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndRolesIn(tenantId, roles).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByEmailPattern(String tenantId, String emailPattern) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndEmailRegex(tenantId, emailPattern).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByNamePattern(String tenantId, String firstName, String lastName) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndFirstNameRegexOrTenantIdAndLastNameRegex(
                tenantId, firstName, lastName).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> searchByKeyword(String tenantId, String keyword) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.searchByTenantIdAndKeyword(tenantId, keyword).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findActive(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndStatus(tenantId, UserProfile.UserStatus.ACTIVE.name()).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<List<UserProfile>> findByPermission(String tenantId, String permission) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndPermission(tenantId, permission).stream()
                .map(UserProfileDocument::toDomain)
                .toList();
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteById(String id) {
        return CompletableFuture.supplyAsync(() -> {
            repository.deleteById(id);
            return true;
        });
    }

    @Override
    public CompletableFuture<Boolean> deleteByUserId(String tenantId, String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<UserProfileDocument> document = repository.findByTenantIdAndUserId(tenantId, userId);
            if (document.isPresent()) {
                repository.delete(document.get());
                return true;
            }
            return false;
        });
    }

    @Override
    public CompletableFuture<Long> countByTenant(String tenantId) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.count();
        });
    }

    @Override
    public CompletableFuture<Long> countByStatus(String tenantId, UserProfile.UserStatus status) {
        return CompletableFuture.supplyAsync(() -> {
            return repository.findByTenantIdAndStatus(tenantId, status.name()).stream()
                .count();
        });
    }
}
