package com.gogidix.rapidassist.orchestration.executiveaggregation.infrastructure.persistence;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;
import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out.ExecutiveDashboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MongoExecutiveDashboardAdapter implements ExecutiveDashboardRepository {

    private final SpringDataExecutiveDashboardRepository repo;

    @Override
    public Optional<ExecutiveDashboard> findLatest() {
        return repo.findTopByOrderByCreatedAtDesc();
    }

    @Override
    public ExecutiveDashboard save(ExecutiveDashboard dashboard) {
        return repo.save(dashboard);
    }
}
