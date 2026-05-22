package com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.out;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;

import java.util.Map;
import java.util.Optional;

public interface ExecutiveDashboardRepository {

    Optional<ExecutiveDashboard> findLatest();

    ExecutiveDashboard save(ExecutiveDashboard dashboard);
}
