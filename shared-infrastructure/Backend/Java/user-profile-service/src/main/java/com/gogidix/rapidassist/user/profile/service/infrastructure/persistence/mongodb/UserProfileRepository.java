package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.mongodb;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfileDocument, String> {

    Optional<UserProfileDocument> findByTenantIdAndUserId(String tenantId, String userId);

    Optional<UserProfileDocument> findByTenantIdAndEmail(String tenantId, String email);

    List<UserProfileDocument> findByTenantId(String tenantId);

    List<UserProfileDocument> findByTenantIdAndRolesContaining(String tenantId, String role);

    List<UserProfileDocument> findByTenantIdAndRolesIn(String tenantId, List<String> roles);

    List<UserProfileDocument> findByTenantIdAndEmailRegex(String tenantId, String emailPattern);

    List<UserProfileDocument> findByTenantIdAndFirstNameRegexOrTenantIdAndLastNameRegex(
        String tenantId, String firstNamePattern, String lastNamePattern);

    @Query("{'tenantId': ?0, $or: [{'userId': {$regex: ?1, $options: 'i'}}, {'email': {$regex: ?1, $options: 'i'}}, {'firstName': {$regex: ?1, $options: 'i'}}, {'lastName': {$regex: ?1, $options: 'i'}}]}")
    List<UserProfileDocument> searchByTenantIdAndKeyword(String tenantId, String keyword);

    List<UserProfileDocument> findByTenantIdAndStatus(String tenantId, String status);

    @Query("{'tenantId': ?0, 'permissions': ?1}")
    List<UserProfileDocument> findByTenantIdAndPermission(String tenantId, String permission);
}
