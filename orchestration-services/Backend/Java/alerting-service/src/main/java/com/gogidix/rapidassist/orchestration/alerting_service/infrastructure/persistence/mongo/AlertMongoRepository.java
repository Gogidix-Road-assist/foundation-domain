package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data MongoDB repository for Alert documents
 */
@Repository
public interface AlertMongoRepository extends MongoRepository<AlertDocument, String> {

    List<AlertDocument> findByAlertId(String alertId);

    List<AlertDocument> findByRequestId(String requestId);

    List<AlertDocument> findByTenantId(String tenantId);

    List<AlertDocument> findByTenantIdAndStatusIn(String tenantId, List<String> statuses);

    List<AlertDocument> findByTenantIdAndTypeAndStatusIn(
        String tenantId,
        String type,
        List<String> statuses
    );

    List<AlertDocument> findBySeverityAndStatusIn(String severity, List<String> statuses);

    List<AlertDocument> findByAssignedToAndStatusIn(String assignedTo, List<String> statuses);

    @Query("{ 'escalationRequired': true, 'status': { $in: ?0 } }")
    List<AlertDocument> findEscalationRequiredByStatusIn(List<String> statuses);

    @Query("{ 'severity': 'CRITICAL', 'status': { $in: ?0 } }")
    List<AlertDocument> findCriticalByStatusIn(List<String> statuses);

    @Query("{ 'createdAt': { $gte: ?1, $lte: ?2 }, 'tenantId': ?0 }")
    List<AlertDocument> findByTenantIdAndCreatedAtBetween(
        String tenantId,
        LocalDateTime start,
        LocalDateTime end
    );

    long countByStatus(String status);

    long countBySeverityAndStatusIn(String severity, List<String> statuses);

    void deleteByAlertId(String alertId);

    boolean existsByAlertId(String alertId);
}
