package com.gogidix.rapidassist.orchestration.transactionorchestrationservice.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.transactionorchestrationservice.domain.model.EntityTracking;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntityTrackingRepository extends MongoRepository<EntityTracking, String> {

    List<EntityTracking> findByEntityIdOrderByEventTimestampDesc(String entityId);
}
