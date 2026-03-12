#!/usr/bin/env python3
"""
Deployment Risk Prediction Model - AI Fraud Detection Service
Financial-Grade Testing Standard - Phase 5

This script predicts deployment risk using:
- Historical deployment data
- Code change metrics
- Test coverage metrics
- Performance baseline deviation

Usage: python deployment-risk-score.py [--metrics-file FILE] [--output FORMAT]
Output:
    DEPLOYMENT_RISK_SCORE={0-100}
    RISK_CATEGORY={LOW|MEDIUM|HIGH|CRITICAL}
    RECOMMENDATION={PROCEED|CAUTION|ABORT}
"""

import argparse
import json
import math
import sys
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Tuple
from enum import Enum


class RiskCategory(Enum):
    LOW = "LOW"
    MEDIUM = "MEDIUM"
    HIGH = "HIGH"
    CRITICAL = "CRITICAL"


class Recommendation(Enum):
    PROCEED = "PROCEED"
    CAUTION = "CAUTION"
    ABORT = "ABORT"


@dataclass
class DeploymentMetrics:
    """Input metrics for risk calculation"""
    # Code metrics
    lines_changed: int = 0
    files_changed: int = 0
    critical_files_changed: int = 0
    test_files_changed: int = 0

    # Test metrics
    line_coverage: float = 0.0
    branch_coverage: float = 0.0
    mutation_score: float = 0.0
    test_pass_rate: float = 100.0

    # Performance metrics
    latency_p95_ms: float = 500.0
    latency_p99_ms: float = 1000.0
    error_rate: float = 0.005
    throughput_rps: float = 1000.0

    # Historical data
    previous_deployment_success_rate: float = 95.0
    mean_time_to_recovery_sec: float = 300.0
    rollback_count_last_30d: int = 0

    # CI/CD metrics
    build_duration_min: float = 10.0
    test_execution_time_min: float = 5.0

    # Dependencies
    dependency_updates: int = 0
    security_vulnerabilities: int = 0


@dataclass
class RiskScore:
    """Calculated risk score with breakdown"""
    total_score: float
    category: RiskCategory
    recommendation: Recommendation

    # Component scores
    code_change_score: float = 0.0
    test_coverage_score: float = 0.0
    performance_score: float = 0.0
    historical_score: float = 0.0
    security_score: float = 0.0

    # Risk factors
    risk_factors: List[str] = field(default_factory=list)
    mitigating_factors: List[str] = field(default_factory=list)


class PredictiveFailureModel:
    """
    Predictive model for deployment failure risk.

    Uses weighted scoring with heuristics tuned for financial-grade services.
    """

    # Weights for different risk components
    WEIGHTS = {
        'code_change': 0.25,
        'test_coverage': 0.30,
        'performance': 0.20,
        'historical': 0.15,
        'security': 0.10,
    }

    # Thresholds
    COVERAGE_THRESHOLDS = {
        'line': 85.0,
        'branch': 75.0,
        'mutation': 60.0,
    }

    PERFORMANCE_THRESHOLDS = {
        'latency_p95': 500.0,
        'latency_p99': 1000.0,
        'error_rate': 0.005,
    }

    def __init__(self):
        self.baseline_metrics: Optional[DeploymentMetrics] = None

    def set_baseline(self, baseline: DeploymentMetrics):
        """Set baseline metrics for comparison"""
        self.baseline_metrics = baseline

    def calculate_risk(self, metrics: DeploymentMetrics) -> RiskScore:
        """Calculate overall deployment risk score"""
        components = {}

        # Calculate individual component scores
        components['code_change'] = self._calculate_code_change_score(metrics)
        components['test_coverage'] = self._calculate_test_coverage_score(metrics)
        components['performance'] = self._calculate_performance_score(metrics)
        components['historical'] = self._calculate_historical_score(metrics)
        components['security'] = self._calculate_security_score(metrics)

        # Calculate weighted total
        total_score = sum(
            components[key] * self.WEIGHTS[key]
            for key in self.WEIGHTS
        )

        # Determine category and recommendation
        category = self._categorize_risk(total_score)
        recommendation = self._make_recommendation(total_score, category, metrics)

        # Collect risk and mitigating factors
        risk_factors, mitigating_factors = self._collect_factors(
            metrics, components
        )

        return RiskScore(
            total_score=round(total_score, 2),
            category=category,
            recommendation=recommendation,
            code_change_score=components['code_change'],
            test_coverage_score=components['test_coverage'],
            performance_score=components['performance'],
            historical_score=components['historical'],
            security_score=components['security'],
            risk_factors=risk_factors,
            mitigating_factors=mitigating_factors,
        )

    def _calculate_code_change_score(self, metrics: DeploymentMetrics) -> float:
        """
        Calculate risk from code changes.

        Factors:
        - Critical files changed (higher risk)
        - Test coverage ratio (tests help reduce risk)
        - Lines changed per file (larger changes = higher risk)
        """
        score = 0.0

        # Critical files impact (0-40 points)
        if metrics.files_changed > 0:
            critical_ratio = metrics.critical_files_changed / metrics.files_changed
            score += critical_ratio * 40.0

        # Test files mitigates risk (-20 points max)
        if metrics.files_changed > 0:
            test_ratio = metrics.test_files_changed / metrics.files_changed
            score -= test_ratio * 20.0

        # Volume of changes (0-30 points)
        if metrics.lines_changed > 1000:
            score += 30.0
        elif metrics.lines_changed > 500:
            score += 20.0
        elif metrics.lines_changed > 100:
            score += 10.0

        # Dependency updates (0-10 points)
        score += min(metrics.dependency_updates * 2, 10.0)

        return max(0, min(100, score))

    def _calculate_test_coverage_score(self, metrics: DeploymentMetrics) -> float:
        """
        Calculate risk from test coverage.

        Lower coverage = Higher risk
        """
        score = 0.0

        # Line coverage penalty (up to 40 points)
        if metrics.line_coverage < self.COVERAGE_THRESHOLDS['line']:
            penalty = (self.COVERAGE_THRESHOLDS['line'] - metrics.line_coverage) / 10
            score += min(penalty * 10, 40.0)

        # Branch coverage penalty (up to 30 points)
        if metrics.branch_coverage < self.COVERAGE_THRESHOLDS['branch']:
            penalty = (self.COVERAGE_THRESHOLDS['branch'] - metrics.branch_coverage) / 10
            score += min(penalty * 10, 30.0)

        # Mutation score penalty (up to 20 points)
        if metrics.mutation_score < self.COVERAGE_THRESHOLDS['mutation']:
            penalty = (self.COVERAGE_THRESHOLDS['mutation'] - metrics.mutation_score) / 10
            score += min(penalty * 10, 20.0)

        # Test pass rate impact (0-10 points)
        if metrics.test_pass_rate < 100:
            score += (100 - metrics.test_pass_rate) / 2

        return max(0, min(100, score))

    def _calculate_performance_score(self, metrics: DeploymentMetrics) -> float:
        """
        Calculate risk from performance metrics.

        Compares against baseline if available.
        """
        score = 0.0

        if self.baseline_metrics:
            # Compare with baseline
            latency_p95_ratio = metrics.latency_p95_ms / self.baseline_metrics.latency_p95_ms
            error_rate_ratio = metrics.error_rate / max(self.baseline_metrics.error_rate, 0.001)

            # Latency degradation
            if latency_p95_ratio > 1.3:
                score += 40.0
            elif latency_p95_ratio > 1.1:
                score += 20.0

            # Error rate increase
            if error_rate_ratio > 2.0:
                score += 40.0
            elif error_rate_ratio > 1.5:
                score += 20.0

            # Throughput decrease
            throughput_ratio = metrics.throughput_rps / max(self.baseline_metrics.throughput_rps, 1.0)
            if throughput_ratio < 0.8:
                score += 20.0
        else:
            # Use absolute thresholds
            if metrics.latency_p95_ms > self.PERFORMANCE_THRESHOLDS['latency_p95'] * 1.5:
                score += 40.0
            elif metrics.latency_p95_ms > self.PERFORMANCE_THRESHOLDS['latency_p95'] * 1.2:
                score += 20.0

            if metrics.error_rate > self.PERFORMANCE_THRESHOLDS['error_rate'] * 2:
                score += 40.0
            elif metrics.error_rate > self.PERFORMANCE_THRESHOLDS['error_rate'] * 1.5:
                score += 20.0

        return max(0, min(100, score))

    def _calculate_historical_score(self, metrics: DeploymentMetrics) -> float:
        """
        Calculate risk from historical deployment data.
        """
        score = 0.0

        # Previous deployment failures
        score += (100 - metrics.previous_deployment_success_rate) / 2

        # Recent rollbacks
        score += min(metrics.rollback_count_last_30d * 10, 30.0)

        # MTTR (longer recovery = higher risk)
        if metrics.mean_time_to_recovery_sec > 600:
            score += 20.0
        elif metrics.mean_time_to_recovery_sec > 300:
            score += 10.0

        return max(0, min(100, score))

    def _calculate_security_score(self, metrics: DeploymentMetrics) -> float:
        """
        Calculate risk from security considerations.
        """
        score = 0.0

        # Security vulnerabilities
        score += min(metrics.security_vulnerabilities * 15, 60.0)

        # Dependency updates without testing (higher risk)
        if metrics.dependency_updates > 0 and metrics.test_files_changed == 0:
            score += metrics.dependency_updates * 5

        return max(0, min(100, score))

    def _categorize_risk(self, score: float) -> RiskCategory:
        """Categorize risk score into levels"""
        if score >= 75:
            return RiskCategory.CRITICAL
        elif score >= 50:
            return RiskCategory.HIGH
        elif score >= 25:
            return RiskCategory.MEDIUM
        else:
            return RiskCategory.LOW

    def _make_recommendation(
        self,
        score: float,
        category: RiskCategory,
        metrics: DeploymentMetrics
    ) -> Recommendation:
        """Make deployment recommendation"""
        # Critical risk always abort
        if category == RiskCategory.CRITICAL:
            return Recommendation.ABORT

        # Security vulnerabilities always abort
        if metrics.security_vulnerabilities > 2:
            return Recommendation.ABORT

        # High risk with low coverage abort
        if category == RiskCategory.HIGH:
            if metrics.line_coverage < self.COVERAGE_THRESHOLDS['line'] - 10:
                return Recommendation.ABORT
            return Recommendation.CAUTION

        # Medium risk caution
        if category == RiskCategory.MEDIUM:
            if metrics.test_pass_rate < 95:
                return Recommendation.CAUTION
            return Recommendation.PROCEED

        # Low risk proceed
        return Recommendation.PROCEED

    def _collect_factors(
        self,
        metrics: DeploymentMetrics,
        components: Dict[str, float]
    ) -> Tuple[List[str], List[str]]:
        """Collect risk and mitigating factors"""
        risk_factors = []
        mitigating_factors = []

        # Code change factors
        if metrics.critical_files_changed > 5:
            risk_factors.append(f"High number of critical files changed: {metrics.critical_files_changed}")
        if metrics.lines_changed > 1000:
            risk_factors.append(f"Large code change: {metrics.lines_changed} lines")
        if metrics.test_files_changed == 0 and metrics.files_changed > 10:
            risk_factors.append("No test files added with significant code changes")

        # Test coverage factors
        if metrics.line_coverage < self.COVERAGE_THRESHOLDS['line']:
            risk_factors.append(f"Line coverage ({metrics.line_coverage}%) below threshold ({self.COVERAGE_THRESHOLDS['line']}%)")
        if metrics.mutation_score < self.COVERAGE_THRESHOLDS['mutation']:
            risk_factors.append(f"Mutation score ({metrics.mutation_score}%) below threshold ({self.COVERAGE_THRESHOLDS['mutation']}%)")

        # Mitigating factors
        if metrics.line_coverage >= 95:
            mitigating_factors.append(f"Excellent line coverage: {metrics.line_coverage}%")
        if metrics.test_files_changed >= metrics.files_changed / 2:
            mitigating_factors.append("Strong test coverage for changes")
        if metrics.test_pass_rate == 100:
            mitigating_factors.append("All tests passing")
        if metrics.previous_deployment_success_rate >= 98:
            mitigating_factors.append("Excellent deployment history")

        # Performance factors
        if self.baseline_metrics:
            latency_change = ((metrics.latency_p95_ms / self.baseline_metrics.latency_p95_ms) - 1) * 100
            if latency_change > 20:
                risk_factors.append(f"Latency increased by {latency_change:.1f}%")
            elif latency_change < -10:
                mitigating_factors.append(f"Latency improved by {abs(latency_change):.1f}%")

        # Security factors
        if metrics.security_vulnerabilities > 0:
            risk_factors.append(f"{metrics.security_vulnerabilities} security vulnerabilities detected")

        return risk_factors, mitigating_factors


def load_metrics_from_file(filepath: str) -> DeploymentMetrics:
    """Load metrics from JSON file"""
    with open(filepath, 'r') as f:
        data = json.load(f)

    return DeploymentMetrics(**data)


def generate_metrics_from_git(repo_path: str = ".") -> DeploymentMetrics:
    """Generate metrics from git repository (stub implementation)"""
    # This would normally use git commands and CI/CD API
    # For now, return default metrics
    return DeploymentMetrics(
        lines_changed=0,
        files_changed=0,
        critical_files_changed=0,
        test_files_changed=0,
        line_coverage=85.0,
        branch_coverage=75.0,
        mutation_score=60.0,
    )


def print_report(risk_score: RiskScore, output_format: str = "text"):
    """Print risk score report"""

    if output_format == "json":
        report = {
            "deployment_risk_score": risk_score.total_score,
            "risk_category": risk_score.category.value,
            "recommendation": risk_score.recommendation.value,
            "component_scores": {
                "code_change": risk_score.code_change_score,
                "test_coverage": risk_score.test_coverage_score,
                "performance": risk_score.performance_score,
                "historical": risk_score.historical_score,
                "security": risk_score.security_score,
            },
            "risk_factors": risk_score.risk_factors,
            "mitigating_factors": risk_score.mitigating_factors,
        }
        print(json.dumps(report, indent=2))
    else:
        # Text format
        print("=" * 60)
        print("DEPLOYMENT RISK ASSESSMENT")
        print("=" * 60)
        print(f"Total Risk Score:     {risk_score.total_score}/100")
        print(f"Risk Category:        {risk_score.category.value}")
        print(f"Recommendation:       {risk_score.recommendation.value}")
        print()
        print("Component Scores:")
        print(f"  Code Changes:        {risk_score.code_change_score:.1f}/100")
        print(f"  Test Coverage:       {risk_score.test_coverage_score:.1f}/100")
        print(f"  Performance:         {risk_score.performance_score:.1f}/100")
        print(f"  Historical:          {risk_score.historical_score:.1f}/100")
        print(f"  Security:            {risk_score.security_score:.1f}/100")
        print()

        if risk_score.risk_factors:
            print("Risk Factors:")
            for factor in risk_score.risk_factors:
                print(f"  - {factor}")
            print()

        if risk_score.mitigating_factors:
            print("Mitigating Factors:")
            for factor in risk_score.mitigating_factors:
                print(f"  + {factor}")
            print()

        # CI/CD output
        print("=" * 60)
        print("CI/CD OUTPUT VARIABLES:")
        print("=" * 60)
        print(f"DEPLOYMENT_RISK_SCORE={risk_score.total_score}")
        print(f"RISK_CATEGORY={risk_score.category.value}")
        print(f"RECOMMENDATION={risk_score.recommendation.value}")


def main():
    parser = argparse.ArgumentParser(description="Calculate deployment risk score")
    parser.add_argument(
        "--metrics-file",
        help="Path to JSON file with deployment metrics"
    )
    parser.add_argument(
        "--baseline-file",
        help="Path to JSON file with baseline metrics"
    )
    parser.add_argument(
        "--output",
        choices=["text", "json"],
        default="text",
        help="Output format (default: text)"
    )

    args = parser.parse_args()

    # Load or generate metrics
    if args.metrics_file:
        metrics = load_metrics_from_file(args.metrics_file)
    else:
        metrics = generate_metrics_from_git()

    # Initialize model and set baseline
    model = PredictiveFailureModel()
    if args.baseline_file:
        baseline = load_metrics_from_file(args.baseline_file)
        model.set_baseline(baseline)

    # Calculate risk
    risk_score = model.calculate_risk(metrics)

    # Print report
    print_report(risk_score, args.output)

    # Exit code based on recommendation
    if risk_score.recommendation == Recommendation.ABORT:
        sys.exit(1)
    elif risk_score.recommendation == Recommendation.CAUTION:
        sys.exit(2)
    else:
        sys.exit(0)


if __name__ == "__main__":
    main()
