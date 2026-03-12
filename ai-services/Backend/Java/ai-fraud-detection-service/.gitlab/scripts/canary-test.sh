#!/bin/bash

###############################################################################
# Canary Comparison Script - AI Fraud Detection Service
# Financial-Grade Testing Standard - Phase 4
#
# Compares metrics between baseline and canary deployments.
# Blocks promotion if deviation exceeds thresholds.
#
# Usage: ./canary-test.sh [baseline_url] [canary_url] [duration_seconds]
# Output:
#   CANARY_STATUS={PASS|FAIL}
#   DEVIATION_SCORE={0-100}
#   BLOCK_PROMOTION={true|false}
###############################################################################

set -euo pipefail

# Configuration
BASELINE_URL="${1:-http://baseline.fraud-detection.svc:8080}"
CANARY_URL="${2:-http://canary.fraud-detection.svc:8080}"
TEST_DURATION="${3:-300}"  # 5 minutes default
SERVICE_NAME="ai-fraud-detection-service"

# Thresholds
FRAUD_SCORE_DEVIATION_THRESHOLD=2.0  # 2%
ERROR_RATE_THRESHOLD=1.5  # 1.5x ratio
LATENCY_THRESHOLD=1.3  # 30% increase
AVAILABILITY_THRESHOLD=0.995  # 99.5%

# Colors
RED='\033[0;31m'
YELLOW='\033[1;33m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m'

###############################################################################
# Helper Functions
###############################################################################

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

log_debug() {
    echo -e "${BLUE}[DEBUG]${NC} $1"
}

# Send test request to fraud detection endpoint
send_fraud_detection_request() {
    local url="$1"
    local entity_id="$2"

    local payload=$(cat <<EOF
{
    "entityType": "CLAIM",
    "entityId": "$entity_id",
    "riskLevel": "HIGH",
    "riskScore": 0.75,
    "detectionMethod": "ML_MODEL",
    "details": {
        "confidence": 0.8,
        "test": "canary"
    }
}
EOF
)

    curl -s -w "\n%{http_code}\n%{time_total}" \
        -X POST \
        -H "Content-Type: application/json" \
        -H "X-Tenant-ID: tenant-canary-test" \
        -d "$payload" \
        "$url/api/v1/fraud-detection/detect" 2>/dev/null
}

# Parse response and extract fraud score
extract_fraud_score() {
    local response="$1"
    echo "$response" | grep -o '"riskScore":[0-9.]*' | cut -d':' -f2
}

# Parse HTTP status code
extract_status_code() {
    local response="$1"
    echo "$response" | tail -n2 | head -n1
}

# Parse response time
extract_response_time() {
    local response="$1"
    echo "$response" | tail -n1
}

# Calculate percentage difference
calculate_percentage_diff() {
    local baseline="$1"
    local canary="$2"

    if [[ $(echo "$baseline == 0" | bc -l) -eq 1 ]]; then
        echo "0"
        return
    fi

    local diff=$(echo "scale=4; ($canary - $baseline) / $baseline * 100" | bc)
    echo "${diff#-}"  # Absolute value
}

# Calculate ratio
calculate_ratio() {
    local baseline="$1"
    local canary="$2"

    if [[ $(echo "$baseline == 0" | bc -l) -eq 1 ]]; then
        echo "1"
        return
    fi

    echo "scale=4; $canary / $baseline" | bc
}

###############################################################################
# Metrics Collection
###############################################################################

collect_metrics() {
    local url="$1"
    local duration="$2"
    local output_file="$3"

    log_info "Collecting metrics from $url for ${duration}s..."

    local total_requests=0
    local successful_requests=0
    local total_score=0
    local total_latency=0
    local start_time=$(date +%s)
    local end_time=$((start_time + duration))

    > "$output_file"

    while [[ $(date +%s) -lt $end_time ]]; do
        local entity_id="test-claim-$total_requests-$(date +%s%N)"
        local response=$(send_fraud_detection_request "$url" "$entity_id")
        local status_code=$(extract_status_code "$response")
        local response_time=$(extract_response_time "$response")
        local fraud_score=$(extract_fraud_score "$response")

        echo "$status_code,$response_time,$fraud_score" >> "$output_file"

        ((total_requests++))

        if [[ "$status_code" == "200" || "$status_code" == "201" ]]; then
            ((successful_requests++))
            total_score=$(echo "scale=4; $total_score + $fraud_score" | bc)
        fi

        total_latency=$(echo "scale=4; $total_latency + $response_time" | bc)

        # Rate limiting
        sleep 0.1
    done

    # Calculate averages
    local success_rate=$(echo "scale=4; $successful_requests / $total_requests" | bc)
    local avg_score=$(echo "scale=4; $total_score / $successful_requests" | bc)
    local avg_latency=$(echo "scale=4; $total_latency / $total_requests" | bc)

    echo "$total_requests,$successful_requests,$success_rate,$avg_score,$avg_latency"
}

###############################################################################
# Comparison Logic
###############################################################################

compare_deployments() {
    local baseline_metrics="$1"
    local canary_metrics="$2"

    log_info "========================================="
    log_info "CANARY ANALYSIS RESULTS"
    log_info "========================================="
    echo ""

    # Parse baseline metrics
    IFS=',' read -r b_total b_success b_rate b_score b_latency <<< "$baseline_metrics"
    local b_availability=$(echo "scale=4; $b_rate * 100" | bc)

    # Parse canary metrics
    IFS=',' read -r c_total c_success c_rate c_score c_latency <<< "$canary_metrics"
    local c_availability=$(echo "scale=4; $c_rate * 100" | bc)

    # Calculate deviations
    local score_deviation=$(calculate_percentage_diff "$b_score" "$c_score")
    local latency_ratio=$(calculate_ratio "$b_latency" "$c_latency")
    local latency_increase=$(echo "scale=2; ($latency_ratio - 1) * 100" | bc)
    local error_rate_ratio=$(calculate_ratio "$(echo "1 - $b_rate" | bc)" "$(echo "1 - $c_rate" | bc)")

    # Display results
    log_info "Baseline: $b_total requests, ${b_availability}% availability, avg score: $b_score, avg latency: ${b_latency}s"
    log_info "Canary:   $c_total requests, ${c_availability}% availability, avg score: $c_score, avg latency: ${c_latency}s"
    echo ""

    # Fraud Score Deviation
    log_info "FRAUD SCORE DEVIATION: ${score_deviation}%"
    if [[ $(echo "$score_deviation > $FRAUD_SCORE_DEVIATION_THRESHOLD" | bc -l) -eq 1 ]]; then
        log_error "  FAIL: Deviation exceeds threshold (${FRAUD_SCORE_DEVIATION_THRESHOLD}%)"
        local fail_score=1
    else
        log_info "  PASS: Deviation within threshold"
        local fail_score=0
    fi
    echo ""

    # Latency Comparison
    log_info "LATENCY RATIO: $latency_ratio (${latency_increase}% increase)"
    if [[ $(echo "$latency_ratio > $LATENCY_THRESHOLD" | bc -l) -eq 1 ]]; then
        log_error "  FAIL: Latency increase exceeds threshold (${LATENCY_THRESHOLD}x)"
        ((fail_score++))
    else
        log_info "  PASS: Latency within threshold"
    fi
    echo ""

    # Error Rate Comparison
    log_info "ERROR RATE RATIO: $error_rate_ratio"
    if [[ $(echo "$error_rate_ratio > $ERROR_RATE_THRESHOLD" | bc -l) -eq 1 ]]; then
        log_error "  FAIL: Error rate increase exceeds threshold (${ERROR_RATE_THRESHOLD}x)"
        ((fail_score++))
    else
        log_info "  PASS: Error rate within threshold"
    fi
    echo ""

    # Availability Check
    log_info "AVAILABILITY CHECK"
    local baseline_avail_pass=$(echo "$b_availability >= $AVAILABILITY_THRESHOLD" | bc -l)
    local canary_avail_pass=$(echo "$c_availability >= $AVAILABILITY_THRESHOLD" | bc -l)

    if [[ $baseline_avail_pass -eq 0 ]]; then
        log_warning "  WARNING: Baseline availability below threshold (${b_availability}% < ${AVAILABILITY_THRESHOLD}%)"
    fi

    if [[ $canary_avail_pass -eq 0 ]]; then
        log_error "  FAIL: Canary availability below threshold (${c_availability}% < ${AVAILABILITY_THRESHOLD}%)"
        ((fail_score++))
    else
        log_info "  PASS: Canary availability meets threshold"
    fi
    echo ""

    # Overall Deviation Score
    local deviation_score=$(echo "scale=2; ($score_deviation + ($latency_ratio - 1) * 100 + ($error_rate_ratio - 1) * 100) / 3" | bc)
    log_info "========================================="
    log_info "OVERALL DEVIATION SCORE: ${deviation_score}%"
    log_info "========================================="
    echo ""

    # Determine final status
    local canary_status="PASS"
    local block_promotion="false"

    if [[ $fail_score -gt 0 ]]; then
        canary_status="FAIL"
        block_promotion="true"
        log_error "CANARY STATUS: FAIL - Promotion BLOCKED"
    else
        log_info "CANARY STATUS: PASS - Promotion can proceed"
    fi

    # Output for CI/CD
    echo ""
    echo "CANARY_STATUS=$canary_status"
    echo "DEVIATION_SCORE=${deviation_score}"
    echo "BLOCK_PROMOTION=$block_promotion"
    echo "FRAUD_SCORE_DEVIATION=${score_deviation}"
    echo "LATENCY_RATIO=${latency_ratio}"
    echo "ERROR_RATE_RATIO=${error_rate_ratio}"

    # Return exit code
    if [[ "$block_promotion" == "true" ]]; then
        return 1
    fi

    return 0
}

###############################################################################
# Detailed Statistical Analysis
###############################################################################

detailed_analysis() {
    local baseline_file="$1"
    local canary_file="$2"

    log_info "========================================="
    log_info "DETAILED STATISTICAL ANALYSIS"
    log_info "========================================="
    echo ""

    # Calculate standard deviations
    local baseline_variance=0
    local canary_variance=0
    local baseline_count=0
    local canary_count=0

    # Calculate mean for baseline
    local baseline_mean=0
    while IFS=',' read -r status time score; do
        if [[ "$status" == "200" || "$status" == "201" ]]; then
            baseline_mean=$(echo "scale=4; $baseline_mean + $score" | bc)
            ((baseline_count++))
        fi
    done < "$baseline_file"
    baseline_mean=$(echo "scale=4; $baseline_mean / $baseline_count" | bc)

    # Calculate variance for baseline
    while IFS=',' read -r status time score; do
        if [[ "$status" == "200" || "$status" == "201" ]]; then
            local diff=$(echo "scale=4; $score - $baseline_mean" | bc)
            baseline_variance=$(echo "scale=4; $baseline_variance + ($diff * $diff)" | bc)
        fi
    done < "$baseline_file"
    local baseline_stddev=$(echo "scale=4; sqrt($baseline_variance / $baseline_count)" | bc)

    # Same for canary
    local canary_mean=0
    while IFS=',' read -r status time score; do
        if [[ "$status" == "200" || "$status" == "201" ]]; then
            canary_mean=$(echo "scale=4; $canary_mean + $score" | bc)
            ((canary_count++))
        fi
    done < "$canary_file"
    canary_mean=$(echo "scale=4; $canary_mean / $canary_count" | bc)

    while IFS=',' read -r status time score; do
        if [[ "$status" == "200" || "$status" == "201" ]]; then
            local diff=$(echo "scale=4; $score - $canary_mean" | bc)
            canary_variance=$(echo "scale=4; $canary_variance + ($diff * $diff)" | bc)
        fi
    done < "$canary_file"
    local canary_stddev=$(echo "scale=4; sqrt($canary_variance / $canary_count)" | bc)

    # Output statistics
    log_info "Baseline Statistics:"
    log_info "  Mean: $baseline_mean"
    log_info "  StdDev: $baseline_stddev"
    log_info "  Count: $baseline_count"
    echo ""

    log_info "Canary Statistics:"
    log_info "  Mean: $canary_mean"
    log_info "  StdDev: $canary_stddev"
    log_info "  Count: $canary_count"
    echo ""

    # T-test approximation (simplified)
    local pooled_stddev=$(echo "scale=4; sqrt((($baseline_stddev^2 * $baseline_count) + ($canary_stddev^2 * $canary_count)) / ($baseline_count + $canary_count))" | bc)
    local t_stat=$(echo "scale=4; ($canary_mean - $baseline_mean) / ($pooled_stddev * sqrt(2))" | bc)

    log_info "Statistical Significance:"
    log_info "  T-statistic: $t_stat"
    if [[ $(echo "$t_stat > 1.96" | bc -l) -eq 1 ]]; then
        log_warning "  Statistically significant difference detected (p < 0.05)"
    else
        log_info "  No statistically significant difference"
    fi
}

###############################################################################
# Main Execution
###############################################################################

main() {
    log_info "Canary Testing - ${SERVICE_NAME}"
    log_info "========================================="
    log_info "Baseline URL: $BASELINE_URL"
    log_info "Canary URL: $CANARY_URL"
    log_info "Test Duration: ${TEST_DURATION}s"
    echo ""

    # Temporary files for metrics
    local baseline_file=$(mktemp)
    local canary_file=$(mktemp)

    trap "rm -f $baseline_file $canary_file" EXIT

    # Check prerequisites
    for cmd in curl bc; do
        if ! command -v "$cmd" &> /dev/null; then
            log_error "Required command not found: $cmd"
            exit 1
        fi
    done

    # Check availability
    log_info "Checking service availability..."
    if ! curl -sf "$BASELINE_URL/actuator/health" > /dev/null 2>&1; then
        log_error "Baseline service is not available"
        exit 1
    fi

    if ! curl -sf "$CANARY_URL/actuator/health" > /dev/null 2>&1; then
        log_error "Canary service is not available"
        exit 1
    fi

    log_info "Both services are available"
    echo ""

    # Collect metrics from both deployments
    local baseline_metrics=$(collect_metrics "$BASELINE_URL" "$TEST_DURATION" "$baseline_file")
    local canary_metrics=$(collect_metrics "$CANARY_URL" "$TEST_DURATION" "$canary_file")

    # Run detailed analysis
    detailed_analysis "$baseline_file" "$canary_file"
    echo ""

    # Compare and determine result
    compare_deployments "$baseline_metrics" "$canary_metrics"
}

# Run main
main "$@"
