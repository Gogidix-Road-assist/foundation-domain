package com.gogidix.rapidassist.orchestration.executiveaggregation.domain.port.in;

import com.gogidix.rapidassist.orchestration.executiveaggregation.domain.model.ExecutiveDashboard;

import java.util.List;
import java.util.Map;

public interface ExecutiveAggregationUseCase {

    ExecutiveDashboard getExecutiveDashboard();

    ExecutiveDashboard refreshExecutiveDashboard();

    Map<String, Object> getGlobalKPIs();

    List<ExecutiveDashboard.CountrySummary> getCountryRankings(String sortBy);

    List<ExecutiveDashboard.Alert> getActiveAlerts();

    Map<String, Object> getRevenueBreakdown();

    Map<String, Object> getOperationalOverview();
}
