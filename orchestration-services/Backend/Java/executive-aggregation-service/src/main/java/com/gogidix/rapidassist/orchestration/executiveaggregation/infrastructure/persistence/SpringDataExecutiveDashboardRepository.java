package com.gogidix.rapidassist.orchestration.executiveaggregation.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SpringDataExecutiveDashboardRepository extends MongoRepository<ExecutiveDashboard, String> {
    Optional<ExecutiveDashboard> findTopByOrderByCreatedAtDesc();
}
