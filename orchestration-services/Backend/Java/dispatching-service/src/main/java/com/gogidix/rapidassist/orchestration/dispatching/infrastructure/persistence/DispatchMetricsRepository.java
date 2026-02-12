package com.gogidix.rapidassist.orchestration.dispatching.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.dispatching.domain.model.DispatchMetrics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchMetricsRepository extends MongoRepository<DispatchMetrics, String> {

    Optional<DispatchMetrics> findByProviderIdAndDate(String providerId, LocalDate date);

    List<DispatchMetrics> findByProviderIdOrderByDateDesc(String providerId);

    List<DispatchMetrics> findByTenantIdAndDateBetween(
        String tenantId,
        LocalDate startDate,
        LocalDate endDate
    );

    List<DispatchMetrics> findByDate(LocalDate date);

    List<DispatchMetrics> findByTenantId(String tenantId);

    List<DispatchMetrics> findTop30ByDateOrderByDateDesc(LocalDate date);
}
